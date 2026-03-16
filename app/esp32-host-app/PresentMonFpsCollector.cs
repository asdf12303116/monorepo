using System.Runtime.InteropServices;

namespace esp32_host_app;

internal readonly record struct FpsSnapshot(int Value, uint ProcessId, DateTimeOffset SampleTime, bool IsStale)
{
    public static FpsSnapshot Empty(uint processId = 0, bool isStale = false)
    {
        return new FpsSnapshot(0, processId, DateTimeOffset.UtcNow, isStale);
    }
}

internal sealed class PresentMonFpsCollector : IDisposable
{
    private const int BytesPerResult = sizeof(double);
    private static readonly TimeSpan QueryWindow = TimeSpan.FromMilliseconds(1000);
    private static readonly TimeSpan PollInterval = TimeSpan.FromMilliseconds(250);
    private static readonly TimeSpan RetryInterval = TimeSpan.FromSeconds(5);
    private static readonly TimeSpan StaleAfter = TimeSpan.FromSeconds(3);
    private static readonly TimeSpan MetricOffset = TimeSpan.FromMilliseconds(32);
    private const uint EtwFlushPeriodMs = 8;
    private static readonly PresentMonMetric[] PreferredMetrics =
    {
        PresentMonMetric.PresentedFps,
        PresentMonMetric.DisplayedFps,
        PresentMonMetric.ApplicationFps
    };

    private sealed class MetricQueryState
    {
        public required PresentMonMetric Metric { get; init; }
        public IntPtr Handle { get; set; }
        public byte[] Buffer { get; set; } = new byte[BytesPerResult * 8];
    }

    private readonly object syncRoot = new();
    private IntPtr sessionHandle;
    private uint trackedProcessId;
    private readonly List<MetricQueryState> metricQueries = new();
    private DateTimeOffset lastInitializationAttempt = DateTimeOffset.MinValue;
    private DateTimeOffset lastPollTime = DateTimeOffset.MinValue;
    private FpsSnapshot latestSnapshot = FpsSnapshot.Empty(isStale: true);
    private string? lastLoggedError;
    private bool isInitialized;
    private bool isDisposed;

    public FpsSnapshot GetSnapshot()
    {
        lock (syncRoot)
        {
            return GetSnapshotCore();
        }
    }

    public void Dispose()
    {
        lock (syncRoot)
        {
            if (isDisposed)
            {
                return;
            }

            TearDownSession();
            isDisposed = true;
        }
    }

    private FpsSnapshot GetSnapshotCore()
    {
        if (!OperatingSystem.IsWindows())
        {
            latestSnapshot = FpsSnapshot.Empty(isStale: true);
            return latestSnapshot;
        }

        EnsureInitialized();
        if (!isInitialized)
        {
            return latestSnapshot with { IsStale = true, Value = 0 };
        }

        var foregroundProcessId = NativeWindow.GetForegroundProcessId();
        if (foregroundProcessId == 0)
        {
            ResetTracking();
            latestSnapshot = FpsSnapshot.Empty(isStale: false);
            return latestSnapshot;
        }

        EnsureTrackedProcess(foregroundProcessId);

        if (DateTimeOffset.UtcNow - lastPollTime >= PollInterval)
        {
            PollLatestFps(foregroundProcessId);
        }

        if (DateTimeOffset.UtcNow - latestSnapshot.SampleTime > StaleAfter)
        {
            latestSnapshot = FpsSnapshot.Empty(foregroundProcessId, isStale: true);
        }

        return latestSnapshot;
    }

    private void EnsureInitialized()
    {
        if (isInitialized || isDisposed)
        {
            return;
        }

        var now = DateTimeOffset.UtcNow;
        if (now - lastInitializationAttempt < RetryInterval)
        {
            return;
        }

        lastInitializationAttempt = now;

        try
        {
            PresentMonNative.EnsureLoaded();
            var openStatus = PresentMonNative.pmOpenSession(out sessionHandle);
            if (openStatus != PresentMonStatus.Success)
            {
                LogInitializationFailure("打开 Session", openStatus);
                TearDownSession();
                return;
            }

            var flushStatus = PresentMonNative.pmSetEtwFlushPeriod(sessionHandle, EtwFlushPeriodMs);
            if (flushStatus != PresentMonStatus.Success)
            {
                LogInitializationFailure("设置 ETW FlushPeriod", flushStatus, $"periodMs={EtwFlushPeriodMs}");
                TearDownSession();
                return;
            }

            var hasRegisteredQuery = false;
            foreach (var metric in PreferredMetrics)
            {
                if (TryRegisterDynamicQuery(metric, out var queryStatus))
                {
                    hasRegisteredQuery = true;
                    continue;
                }

                LogInitializationFailure("注册 FPS 查询", queryStatus, $"metric={metric}");
            }

            if (!hasRegisteredQuery)
            {
                TearDownSession();
                return;
            }

            isInitialized = true;
            lastLoggedError = null;
            Console.WriteLine("PresentMon 初始化成功: 已建立 Session 并注册 FPS 查询");
        }
        catch (DllNotFoundException ex)
        {
            LogInitializationException(
                "加载 Loader",
                $"未找到 PresentMon Loader，应用将继续运行但 FPS 会保持为 0。请确认 PresentMonAPI2Loader.dll 已部署，或设置 PRESENTMON_LOADER_DLL / PRESENTMON_SDK_DIR。详情: {ex.Message}");
            TearDownSession();
        }
        catch (EntryPointNotFoundException ex)
        {
            LogInitializationException(
                "解析 Loader 接口",
                $"PresentMon Loader 缺少所需导出函数，可能是 SDK 版本不匹配。应用将继续运行但 FPS 会保持为 0。详情: {ex.Message}");
            TearDownSession();
        }
        catch (Exception ex)
        {
            LogInitializationException("初始化", $"{ex.GetType().Name}: {ex.Message}");
            TearDownSession();
        }
    }

    private bool TryRegisterDynamicQuery(PresentMonMetric metric, out PresentMonStatus status)
    {
        if (sessionHandle == IntPtr.Zero)
        {
            status = PresentMonStatus.SessionNotOpen;
            return false;
        }

        var elements = new[]
        {
            new PmQueryElement
            {
                Metric = metric,
                Stat = PresentMonStat.Avg,
                DeviceId = 0,
                ArrayIndex = 0,
                DataOffset = 0,
                DataSize = BytesPerResult
            }
        };

        status = PresentMonNative.pmRegisterDynamicQuery(
            sessionHandle,
            out var queryHandle,
            elements,
            1,
            QueryWindow.TotalMilliseconds,
            MetricOffset.TotalMilliseconds);

        if (status != PresentMonStatus.Success)
        {
            return false;
        }

        metricQueries.Add(new MetricQueryState
        {
            Metric = metric,
            Handle = queryHandle
        });
        return true;
    }

    private void EnsureTrackedProcess(uint processId)
    {
        if (trackedProcessId == processId)
        {
            return;
        }

        if (trackedProcessId != 0 && sessionHandle != IntPtr.Zero)
        {
            PresentMonNative.pmStopTrackingProcess(sessionHandle, trackedProcessId);
        }

        var status = PresentMonNative.pmStartTrackingProcess(sessionHandle, processId);
        if (status == PresentMonStatus.Success || status == PresentMonStatus.AlreadyTrackingProcess)
        {
            trackedProcessId = processId;
            latestSnapshot = FpsSnapshot.Empty(processId, isStale: false);
            lastLoggedError = null;
            return;
        }

        trackedProcessId = 0;
        latestSnapshot = FpsSnapshot.Empty(processId, isStale: false);
        LogErrorOnce($"开始跟踪前台进程失败: pid={processId}, status={status}");

        if (status is PresentMonStatus.BadHandle or PresentMonStatus.SessionNotOpen or PresentMonStatus.ServiceError or PresentMonStatus.PipeError)
        {
            TearDownSession();
        }
    }

    private void PollLatestFps(uint processId)
    {
        if (metricQueries.Count == 0)
        {
            latestSnapshot = FpsSnapshot.Empty(processId, isStale: false);
            return;
        }

        lastPollTime = DateTimeOffset.UtcNow;
        var pollResults = new Dictionary<PresentMonMetric, int>(metricQueries.Count);

        foreach (var query in metricQueries)
        {
            var status = PollDynamicQuery(query, processId, out var fps);
            pollResults[query.Metric] = fps;

            if (status is PresentMonStatus.BadHandle or PresentMonStatus.SessionNotOpen or PresentMonStatus.ServiceError or PresentMonStatus.PipeError)
            {
                LogErrorOnce($"轮询 PresentMon FPS 失败: metric={query.Metric}, status={status}");
                TearDownSession();
                latestSnapshot = FpsSnapshot.Empty(processId, isStale: false);
                return;
            }
        }

        if (TryGetPreferredFps(pollResults, out var preferredFps))
        {
            latestSnapshot = new FpsSnapshot(preferredFps, processId, DateTimeOffset.UtcNow, false);
            lastLoggedError = null;
            return;
        }

        latestSnapshot = new FpsSnapshot(0, processId, DateTimeOffset.UtcNow, false);
        lastLoggedError = null;
    }

    private PresentMonStatus PollDynamicQuery(
        MetricQueryState query,
        uint processId,
        out int fps)
    {
        while (true)
        {
            var swapChainCount = (uint)(query.Buffer.Length / BytesPerResult);
            var status = PresentMonNative.pmPollDynamicQuery(query.Handle, processId, query.Buffer, ref swapChainCount);

            if (status == PresentMonStatus.InsufficientBuffer && swapChainCount > 0)
            {
                query.Buffer = new byte[swapChainCount * BytesPerResult];
                continue;
            }

            fps = ExtractFpsValue(query.Buffer, swapChainCount);
            return status;
        }
    }

    private static int ExtractFpsValue(byte[] buffer, uint swapChainCount)
    {
        if (swapChainCount == 0)
        {
            return 0;
        }

        var bestValue = 0d;
        var maxResults = Math.Min((int)swapChainCount, buffer.Length / BytesPerResult);

        for (var index = 0; index < maxResults; index++)
        {
            var offset = index * BytesPerResult;
            var value = BitConverter.ToDouble(buffer, offset);
            if (double.IsNaN(value) || double.IsInfinity(value))
            {
                continue;
            }

            if (value > bestValue)
            {
                bestValue = value;
            }
        }

        return (int)Math.Round(bestValue, MidpointRounding.AwayFromZero);
    }

    private void ResetTracking()
    {
        if (trackedProcessId == 0 || sessionHandle == IntPtr.Zero)
        {
            trackedProcessId = 0;
            return;
        }

        PresentMonNative.pmStopTrackingProcess(sessionHandle, trackedProcessId);
        trackedProcessId = 0;
    }

    private void TearDownSession()
    {
        ResetTracking();

        foreach (var query in metricQueries)
        {
            if (query.Handle == IntPtr.Zero)
            {
                continue;
            }

            PresentMonNative.pmFreeDynamicQuery(query.Handle);
            query.Handle = IntPtr.Zero;
        }

        metricQueries.Clear();

        if (sessionHandle != IntPtr.Zero)
        {
            PresentMonNative.pmCloseSession(sessionHandle);
            sessionHandle = IntPtr.Zero;
        }

        isInitialized = false;
    }

    private void LogErrorOnce(string message)
    {
        if (string.Equals(lastLoggedError, message, StringComparison.Ordinal))
        {
            return;
        }

        lastLoggedError = message;
        Console.WriteLine(message);
    }

    private void LogInitializationFailure(string stage, PresentMonStatus status, string? extra = null)
    {
        var message = $"PresentMon 初始化失败: stage={stage}, status={status}, detail={DescribeStatus(status)}";
        if (!string.IsNullOrWhiteSpace(extra))
        {
            message += $", extra={extra}";
        }

        LogErrorOnce(message);
    }

    private void LogInitializationException(string stage, string detail)
    {
        LogErrorOnce($"PresentMon 初始化失败: stage={stage}, detail={detail}");
    }

    private static string DescribeStatus(PresentMonStatus status)
    {
        return status switch
        {
            PresentMonStatus.Success => "成功",
            PresentMonStatus.Failure => "通用失败",
            PresentMonStatus.BadArgument => "传入参数无效",
            PresentMonStatus.BadHandle => "句柄无效，通常表示 Session 或 Query 已失效",
            PresentMonStatus.ServiceError => "PresentMon Service 未运行、未安装或当前无法连接",
            PresentMonStatus.InvalidEtlFile => "ETL 文件无效",
            PresentMonStatus.InvalidPid => "进程 ID 无效或目标进程不可跟踪",
            PresentMonStatus.AlreadyTrackingProcess => "该进程已在跟踪中",
            PresentMonStatus.UnableToCreateNsm => "无法创建共享内存",
            PresentMonStatus.InvalidAdapterId => "显卡适配器 ID 无效",
            PresentMonStatus.OutOfRange => "参数超出允许范围",
            PresentMonStatus.InsufficientBuffer => "结果缓冲区不足",
            PresentMonStatus.PipeError => "与 PresentMon Service 的通信管道失败",
            PresentMonStatus.SessionNotOpen => "Session 尚未建立，通常是前置初始化失败",
            PresentMonStatus.MiddlewareMissingPath => "未提供 middleware 路径",
            PresentMonStatus.NonexistentFilePath => "找不到 PresentMon Loader 或 middleware 文件",
            PresentMonStatus.MiddlewareInvalidSignature => "Loader 或 middleware 签名无效",
            PresentMonStatus.MiddlewareMissingEndpoint => "Loader 或 middleware 缺少所需接口",
            PresentMonStatus.MiddlewareVersionLow => "Loader 或 middleware 版本过低",
            PresentMonStatus.MiddlewareVersionHigh => "Loader 或 middleware 版本过高",
            PresentMonStatus.MiddlewareServiceMismatch => "Loader、middleware 与 Service 版本不兼容",
            _ => "未知状态"
        };
    }

    private static bool TryGetPreferredFps(IReadOnlyDictionary<PresentMonMetric, int> pollResults, out int fps)
    {
        foreach (var metric in PreferredMetrics)
        {
            if (pollResults.TryGetValue(metric, out fps) && fps > 0)
            {
                return true;
            }
        }

        fps = 0;
        return false;
    }

    private static class NativeWindow
    {
        [DllImport("user32.dll", SetLastError = true)]
        private static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll", SetLastError = true)]
        private static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        public static uint GetForegroundProcessId()
        {
            var windowHandle = GetForegroundWindow();
            if (windowHandle == IntPtr.Zero)
            {
                return 0;
            }

            GetWindowThreadProcessId(windowHandle, out var processId);
            return processId;
        }
    }
}
