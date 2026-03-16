using System.Reflection;
using System.Runtime.InteropServices;

namespace esp32_host_app;

internal enum PresentMonStatus
{
    Success = 0,
    Failure = 1,
    BadArgument = 2,
    BadHandle = 3,
    ServiceError = 4,
    InvalidEtlFile = 5,
    InvalidPid = 6,
    AlreadyTrackingProcess = 7,
    UnableToCreateNsm = 8,
    InvalidAdapterId = 9,
    OutOfRange = 10,
    InsufficientBuffer = 11,
    PipeError = 12,
    SessionNotOpen = 13,
    MiddlewareMissingPath = 14,
    NonexistentFilePath = 15,
    MiddlewareInvalidSignature = 16,
    MiddlewareMissingEndpoint = 17,
    MiddlewareVersionLow = 18,
    MiddlewareVersionHigh = 19,
    MiddlewareServiceMismatch = 20
}

internal enum PresentMonMetric
{
    DisplayedFps = 11,
    PresentedFps = 12,
    ApplicationFps = 62
}

internal enum PresentMonStat
{
    None = 0,
    Avg = 1
}

[StructLayout(LayoutKind.Sequential)]
internal struct PmQueryElement
{
    public PresentMonMetric Metric;
    public PresentMonStat Stat;
    public uint DeviceId;
    public uint ArrayIndex;
    public ulong DataOffset;
    public ulong DataSize;
}

internal static class PresentMonNative
{
    private const string LoaderFileName = "PresentMonAPI2Loader.dll";
    private const string DefaultSdkDirectory = @"C:\Program Files\Intel\PresentMon\SDK";

    static PresentMonNative()
    {
        NativeLibrary.SetDllImportResolver(
            typeof(PresentMonNative).Assembly,
            ResolveLibrary);
    }

    public static void EnsureLoaded()
    {
    }

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmOpenSession(out IntPtr handle);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmCloseSession(IntPtr handle);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmStartTrackingProcess(IntPtr handle, uint processId);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmStopTrackingProcess(IntPtr handle, uint processId);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmRegisterDynamicQuery(
        IntPtr sessionHandle,
        out IntPtr handle,
        [In] PmQueryElement[] elements,
        ulong numElements,
        double windowSizeMs,
        double metricOffsetMs);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmSetEtwFlushPeriod(IntPtr handle, uint periodMs);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmFreeDynamicQuery(IntPtr handle);

    [DllImport(LoaderFileName, CallingConvention = CallingConvention.Cdecl, ExactSpelling = true)]
    internal static extern PresentMonStatus pmPollDynamicQuery(
        IntPtr handle,
        uint processId,
        [Out] byte[] blob,
        ref uint numSwapChains);

    private static IntPtr ResolveLibrary(string libraryName, Assembly assembly, DllImportSearchPath? searchPath)
    {
        if (!string.Equals(Path.GetFileName(libraryName), LoaderFileName, StringComparison.OrdinalIgnoreCase))
        {
            return IntPtr.Zero;
        }

        foreach (var candidatePath in GetCandidatePaths())
        {
            if (!File.Exists(candidatePath))
            {
                continue;
            }

            if (NativeLibrary.TryLoad(candidatePath, out var handle))
            {
                return handle;
            }
        }

        NativeLibrary.TryLoad(libraryName, assembly, searchPath, out var fallbackHandle);
        return fallbackHandle;
    }

    private static IEnumerable<string> GetCandidatePaths()
    {
        yield return Path.Combine(AppContext.BaseDirectory, LoaderFileName);

        var configuredLoaderPath = GetEnvironmentVariable("PRESENTMON_LOADER_DLL", "PresentMonLoaderDll");
        if (!string.IsNullOrWhiteSpace(configuredLoaderPath))
        {
            yield return configuredLoaderPath;
        }

        var configuredSdkDirectory = GetEnvironmentVariable("PRESENTMON_SDK_DIR", "PresentMonSdkDir");
        if (!string.IsNullOrWhiteSpace(configuredSdkDirectory))
        {
            yield return Path.Combine(configuredSdkDirectory, LoaderFileName);
        }

        yield return Path.Combine(DefaultSdkDirectory, LoaderFileName);
    }

    private static string? GetEnvironmentVariable(params string[] keys)
    {
        foreach (var key in keys)
        {
            var value = Environment.GetEnvironmentVariable(key);
            if (!string.IsNullOrWhiteSpace(value))
            {
                return value;
            }
        }

        return null;
    }
}
