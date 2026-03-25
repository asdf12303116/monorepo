using System.Diagnostics;
using System.Globalization;

namespace esp32_host_app;

internal sealed class LinuxHardwareMonitor
{
    private const string ProcStatPath = "/proc/stat";
    private const string ProcCpuInfoPath = "/proc/cpuinfo";
    private const string ProcMemInfoPath = "/proc/meminfo";
    private const string CpuSysPath = "/sys/devices/system/cpu";
    private const string ThermalSysPath = "/sys/class/thermal";
    private const string PowerCapSysPath = "/sys/class/powercap";

    private readonly HashSet<int> efficientCpuIds = LoadCpuSet("/sys/devices/cpu_atom/cpus");
    private readonly Dictionary<int, CpuTimes> previousCpuTimes = [];
    private readonly string? raplEnergyPath = FindRaplEnergyPath();

    private ulong? previousPackageEnergyMicrojoules;
    private DateTimeOffset previousPackageEnergyTime = DateTimeOffset.MinValue;

    public HardwareSnapshot Capture()
    {
        var snapshot = new HardwareSnapshot();

        PopulateCpu(snapshot);
        PopulateMemory(snapshot);
        PopulateGpu(snapshot);

        return snapshot;
    }

    private void PopulateCpu(HardwareSnapshot snapshot)
    {
        var cpuTimes = ReadCpuTimes();
        if (cpuTimes.Count == 0)
        {
            return;
        }

        if (cpuTimes.TryGetValue(-1, out var aggregateTimes))
        {
            snapshot.CpuAverageUsage = CalculateUsagePercent(-1, aggregateTimes);
        }

        foreach (var entry in cpuTimes.Where(x => x.Key >= 0).OrderBy(x => x.Key))
        {
            snapshot.CpuCores.Add(new CpuCoreSnapshot
            {
                CoreId = entry.Key,
                IsEfficientCore = efficientCpuIds.Contains(entry.Key),
                FrequencyMHz = ReadCpuFrequencyMHz(entry.Key),
                UsagePercent = CalculateUsagePercent(entry.Key, entry.Value)
            });
        }

        snapshot.CpuTemperature = ReadCpuTemperatureCelsius();
        snapshot.CpuPower = ReadCpuPowerWatts();
    }

    private void PopulateMemory(HardwareSnapshot snapshot)
    {
        if (!File.Exists(ProcMemInfoPath))
        {
            return;
        }

        var memInfo = File.ReadLines(ProcMemInfoPath)
            .Select(ParseKeyValueLine)
            .Where(x => x.Key.Length > 0)
            .ToDictionary(x => x.Key, x => x.Value, StringComparer.OrdinalIgnoreCase);

        if (!memInfo.TryGetValue("MemTotal", out var totalKilobytes) || totalKilobytes <= 0)
        {
            return;
        }

        memInfo.TryGetValue("MemAvailable", out var availableKilobytes);
        var usedKilobytes = Math.Max(0, totalKilobytes - availableKilobytes);

        snapshot.Memory = new MemorySnapshot
        {
            UsedGigabytes = (float)(usedKilobytes / 1024d / 1024d),
            UsagePercent = (float)(usedKilobytes * 100d / totalKilobytes)
        };
    }

    private void PopulateGpu(HardwareSnapshot snapshot)
    {
        const string query =
            "--query-gpu=utilization.gpu,temperature.gpu,power.draw,clocks.current.graphics,clocks.current.memory,memory.used,memory.total --format=csv,noheader,nounits";
        var output = RunCommand("nvidia-smi", query);
        if (string.IsNullOrWhiteSpace(output))
        {
            snapshot.Gpu = CreateEmptyGpuSnapshot();
            return;
        }

        var firstLine = output.Split('\n', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries)
            .FirstOrDefault();
        if (string.IsNullOrWhiteSpace(firstLine))
        {
            snapshot.Gpu = CreateEmptyGpuSnapshot();
            return;
        }

        var parts = firstLine.Split(',', StringSplitOptions.TrimEntries);
        if (parts.Length < 7)
        {
            snapshot.Gpu = CreateEmptyGpuSnapshot();
            return;
        }

        var memoryUsedMiB = ParseNullableFloat(parts[5]);
        var memoryTotalMiB = ParseNullableFloat(parts[6]);
        float? memoryUsagePercent = null;
        if (memoryUsedMiB.HasValue && memoryTotalMiB is > 0)
        {
            memoryUsagePercent = memoryUsedMiB.Value * 100f / memoryTotalMiB.Value;
        }

        snapshot.Gpu = new GpuSnapshot
        {
            CoreUsagePercent = ParseNullableFloat(parts[0]),
            TemperatureCelsius = ParseNullableFloat(parts[1]),
            PowerWatts = ParseNullableFloat(parts[2]),
            CoreFrequencyMHz = ParseNullableFloat(parts[3]),
            MemoryFrequencyMHz = ParseNullableFloat(parts[4]),
            MemoryUsedMiB = memoryUsedMiB,
            MemoryUsagePercent = memoryUsagePercent,
            CoreVoltageMillivolts = 0,
            LimitHeat = string.Empty,
            LimitPower = string.Empty
        };
    }

    private Dictionary<int, CpuTimes> ReadCpuTimes()
    {
        if (!File.Exists(ProcStatPath))
        {
            return [];
        }

        var result = new Dictionary<int, CpuTimes>();
        foreach (var line in File.ReadLines(ProcStatPath))
        {
            if (!line.StartsWith("cpu", StringComparison.Ordinal))
            {
                continue;
            }

            var parts = line.Split(' ', StringSplitOptions.RemoveEmptyEntries);
            if (parts.Length < 5)
            {
                continue;
            }

            var cpuId = ParseCpuId(parts[0]);
            if (cpuId == null)
            {
                continue;
            }

            if (!TryParseCpuTimes(parts, out var cpuTimes))
            {
                continue;
            }

            result[cpuId.Value] = cpuTimes;
        }

        return result;
    }

    private float? CalculateUsagePercent(int cpuId, CpuTimes current)
    {
        if (!previousCpuTimes.TryGetValue(cpuId, out var previous))
        {
            previousCpuTimes[cpuId] = current;
            return 0;
        }

        previousCpuTimes[cpuId] = current;

        var totalDelta = current.Total - previous.Total;
        var idleDelta = current.Idle - previous.Idle;
        if (totalDelta == 0)
        {
            return 0;
        }

        var usage = (totalDelta - idleDelta) * 100d / totalDelta;
        return (float)Math.Clamp(usage, 0d, 100d);
    }

    private static int? ParseCpuId(string token)
    {
        if (string.Equals(token, "cpu", StringComparison.Ordinal))
        {
            return -1;
        }

        if (token.StartsWith("cpu", StringComparison.Ordinal) &&
            int.TryParse(token.AsSpan(3), NumberStyles.Integer, CultureInfo.InvariantCulture, out var cpuId))
        {
            return cpuId;
        }

        return null;
    }

    private static bool TryParseCpuTimes(string[] parts, out CpuTimes cpuTimes)
    {
        cpuTimes = default;
        var values = new ulong[10];
        for (var i = 1; i < parts.Length && i <= values.Length; i++)
        {
            if (!ulong.TryParse(parts[i], NumberStyles.Integer, CultureInfo.InvariantCulture, out values[i - 1]))
            {
                return false;
            }
        }

        cpuTimes = new CpuTimes(
            values[0],
            values[1],
            values[2],
            values[3],
            values[4],
            values[5],
            values[6],
            values[7]);
        return true;
    }

    private float? ReadCpuFrequencyMHz(int cpuId)
    {
        var frequencyPaths = new[]
        {
            Path.Combine(CpuSysPath, $"cpu{cpuId}", "cpufreq", "scaling_cur_freq"),
            Path.Combine(CpuSysPath, $"cpu{cpuId}", "cpufreq", "cpuinfo_cur_freq")
        };

        foreach (var path in frequencyPaths)
        {
            if (!File.Exists(path))
            {
                continue;
            }

            var raw = File.ReadAllText(path).Trim();
            if (float.TryParse(raw, NumberStyles.Float, CultureInfo.InvariantCulture, out var valueKHz))
            {
                return valueKHz / 1000f;
            }
        }

        return ReadCpuFrequencyFromProcCpuInfo(cpuId);
    }

    private float? ReadCpuFrequencyFromProcCpuInfo(int cpuId)
    {
        if (!File.Exists(ProcCpuInfoPath))
        {
            return null;
        }

        var currentProcessor = -1;
        foreach (var line in File.ReadLines(ProcCpuInfoPath))
        {
            if (line.StartsWith("processor", StringComparison.OrdinalIgnoreCase))
            {
                currentProcessor = (int)ParseKeyValueLine(line).Value;
                continue;
            }

            if (currentProcessor != cpuId || !line.StartsWith("cpu MHz", StringComparison.OrdinalIgnoreCase))
            {
                continue;
            }

            var value = line.Split(':', 2)[1].Trim();
            return ParseNullableFloat(value);
        }

        return null;
    }

    private float? ReadCpuTemperatureCelsius()
    {
        if (!Directory.Exists(ThermalSysPath))
        {
            return null;
        }

        var preferredCandidates = new List<float>();
        var fallbackCandidates = new List<float>();

        foreach (var zonePath in Directory.EnumerateDirectories(ThermalSysPath, "thermal_zone*"))
        {
            var typePath = Path.Combine(zonePath, "type");
            var tempPath = Path.Combine(zonePath, "temp");
            if (!File.Exists(tempPath))
            {
                continue;
            }

            var type = File.Exists(typePath) ? File.ReadAllText(typePath).Trim().ToLowerInvariant() : string.Empty;
            var tempValue = File.ReadAllText(tempPath).Trim();
            if (!float.TryParse(tempValue, NumberStyles.Float, CultureInfo.InvariantCulture, out var milliCelsius))
            {
                continue;
            }

            var celsius = milliCelsius / 1000f;
            if (celsius <= 0 || celsius > 130)
            {
                continue;
            }

            if (type.Contains("x86_pkg_temp", StringComparison.Ordinal) ||
                type.Contains("package", StringComparison.Ordinal) ||
                type.Contains("cpu", StringComparison.Ordinal) ||
                type.Contains("coretemp", StringComparison.Ordinal) ||
                type.Contains("k10temp", StringComparison.Ordinal) ||
                type.Contains("tctl", StringComparison.Ordinal))
            {
                preferredCandidates.Add(celsius);
            }
            else
            {
                fallbackCandidates.Add(celsius);
            }
        }

        if (preferredCandidates.Count > 0)
        {
            return preferredCandidates.Max();
        }

        return fallbackCandidates.Count > 0 ? fallbackCandidates.Max() : null;
    }

    private float? ReadCpuPowerWatts()
    {
        if (string.IsNullOrEmpty(raplEnergyPath) || !File.Exists(raplEnergyPath))
        {
            return null;
        }

        string raw;
        try
        {
            raw = File.ReadAllText(raplEnergyPath).Trim();
        }
        catch (UnauthorizedAccessException)
        {
            return null;
        }
        catch (IOException)
        {
            return null;
        }

        if (!ulong.TryParse(raw, NumberStyles.Integer, CultureInfo.InvariantCulture, out var currentEnergyMicrojoules))
        {
            return null;
        }

        var now = DateTimeOffset.UtcNow;
        if (!previousPackageEnergyMicrojoules.HasValue || previousPackageEnergyTime == DateTimeOffset.MinValue)
        {
            previousPackageEnergyMicrojoules = currentEnergyMicrojoules;
            previousPackageEnergyTime = now;
            return null;
        }

        var deltaTimeSeconds = (now - previousPackageEnergyTime).TotalSeconds;
        var deltaEnergyMicrojoules = currentEnergyMicrojoules >= previousPackageEnergyMicrojoules.Value
            ? currentEnergyMicrojoules - previousPackageEnergyMicrojoules.Value
            : 0;

        previousPackageEnergyMicrojoules = currentEnergyMicrojoules;
        previousPackageEnergyTime = now;

        if (deltaTimeSeconds <= 0 || deltaEnergyMicrojoules == 0)
        {
            return null;
        }

        var watts = deltaEnergyMicrojoules / 1_000_000d / deltaTimeSeconds;
        return (float)watts;
    }

    private static string? FindRaplEnergyPath()
    {
        var packageRoot = Path.Combine(PowerCapSysPath, "intel-rapl:0");
        if (!Directory.Exists(packageRoot))
        {
            return null;
        }

        try
        {
            var rootEnergyPath = Path.Combine(packageRoot, "energy_uj");
            var rootNamePath = Path.Combine(packageRoot, "name");
            if (File.Exists(rootEnergyPath) &&
                File.Exists(rootNamePath) &&
                File.ReadAllText(rootNamePath).Contains("package", StringComparison.OrdinalIgnoreCase))
            {
                return rootEnergyPath;
            }

            var childDirectories = Directory.EnumerateDirectories(packageRoot, "intel-rapl:*", SearchOption.TopDirectoryOnly)
                .ToList();

            foreach (var childDirectory in childDirectories)
            {
                var energyPath = Path.Combine(childDirectory, "energy_uj");
                var namePath = Path.Combine(childDirectory, "name");
                if (!File.Exists(energyPath))
                {
                    continue;
                }

                if (File.Exists(namePath) &&
                    File.ReadAllText(namePath).Contains("package", StringComparison.OrdinalIgnoreCase))
                {
                    return energyPath;
                }
            }

            if (File.Exists(rootEnergyPath))
            {
                return rootEnergyPath;
            }

            return childDirectories
                .Select(directory => Path.Combine(directory, "energy_uj"))
                .FirstOrDefault(File.Exists);
        }
        catch
        {
            return null;
        }
    }

    private static HashSet<int> LoadCpuSet(string path)
    {
        if (!File.Exists(path))
        {
            return [];
        }

        return CpuSetParser.Parse(File.ReadAllText(path));
    }

    private static KeyValuePair<string, long> ParseKeyValueLine(string line)
    {
        var parts = line.Split(':', 2, StringSplitOptions.TrimEntries);
        if (parts.Length != 2)
        {
            return KeyValuePair.Create(string.Empty, 0L);
        }

        var rawValue = new string(parts[1].TakeWhile(ch => char.IsDigit(ch) || ch == '-' || ch == ' ').ToArray()).Trim();
        return long.TryParse(rawValue, NumberStyles.Integer, CultureInfo.InvariantCulture, out var value)
            ? KeyValuePair.Create(parts[0], value)
            : KeyValuePair.Create(parts[0], 0L);
    }

    private static float? ParseNullableFloat(string value)
    {
        return float.TryParse(value.Trim(), NumberStyles.Float, CultureInfo.InvariantCulture, out var result)
            ? result
            : null;
    }

    private static GpuSnapshot CreateEmptyGpuSnapshot()
    {
        return new GpuSnapshot
        {
            CoreFrequencyMHz = 0,
            CoreUsagePercent = 0,
            CoreVoltageMillivolts = 0,
            LimitHeat = string.Empty,
            LimitPower = string.Empty,
            MemoryFrequencyMHz = 0,
            MemoryUsedMiB = 0,
            MemoryUsagePercent = 0,
            PowerWatts = 0,
            TemperatureCelsius = 0
        };
    }

    private static string? RunCommand(string fileName, string arguments)
    {
        try
        {
            using var process = new Process
            {
                StartInfo = new ProcessStartInfo
                {
                    FileName = fileName,
                    Arguments = arguments,
                    RedirectStandardOutput = true,
                    RedirectStandardError = true,
                    UseShellExecute = false,
                    CreateNoWindow = true
                }
            };

            process.Start();
            if (!process.WaitForExit(2000))
            {
                process.Kill(entireProcessTree: true);
                return null;
            }

            if (process.ExitCode != 0)
            {
                return null;
            }

            return process.StandardOutput.ReadToEnd();
        }
        catch
        {
            return null;
        }
    }

    private readonly record struct CpuTimes(
        ulong User,
        ulong Nice,
        ulong System,
        ulong IdleTicks,
        ulong IoWait,
        ulong Irq,
        ulong SoftIrq,
        ulong Steal)
    {
        public ulong Idle => IdleTicks + IoWait;
        public ulong Total => User + Nice + System + IdleTicks + IoWait + Irq + SoftIrq + Steal;
    }
}

internal static class CpuSetParser
{
    public static HashSet<int> Parse(string text)
    {
        var result = new HashSet<int>();
        foreach (var segment in text.Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries))
        {
            var bounds = segment.Split('-', 2, StringSplitOptions.TrimEntries);
            if (bounds.Length == 1 &&
                int.TryParse(bounds[0], NumberStyles.Integer, CultureInfo.InvariantCulture, out var cpuId))
            {
                result.Add(cpuId);
                continue;
            }

            if (bounds.Length != 2 ||
                !int.TryParse(bounds[0], NumberStyles.Integer, CultureInfo.InvariantCulture, out var start) ||
                !int.TryParse(bounds[1], NumberStyles.Integer, CultureInfo.InvariantCulture, out var end))
            {
                continue;
            }

            for (var cpu = Math.Min(start, end); cpu <= Math.Max(start, end); cpu++)
            {
                result.Add(cpu);
            }
        }

        return result;
    }
}
