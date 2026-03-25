namespace esp32_host_app;

public sealed class HardwareSnapshot
{
    public float? CpuAverageUsage { get; set; }
    public float? CpuTemperature { get; set; }
    public float? CpuPower { get; set; }
    public List<CpuCoreSnapshot> CpuCores { get; } = [];
    public MemorySnapshot Memory { get; set; } = new();
    public GpuSnapshot Gpu { get; set; } = new();
}

public sealed class CpuCoreSnapshot
{
    public int CoreId { get; set; }
    public bool IsEfficientCore { get; set; }
    public float? FrequencyMHz { get; set; }
    public float? UsagePercent { get; set; }
}

public sealed class MemorySnapshot
{
    public float? UsedGigabytes { get; set; }
    public float? UsagePercent { get; set; }
}

public sealed class GpuSnapshot
{
    public float? CoreFrequencyMHz { get; set; }
    public float? CoreUsagePercent { get; set; }
    public float? CoreVoltageMillivolts { get; set; }
    public string LimitHeat { get; set; } = string.Empty;
    public string LimitPower { get; set; } = string.Empty;
    public float? MemoryFrequencyMHz { get; set; }
    public float? MemoryUsedMiB { get; set; }
    public float? MemoryUsagePercent { get; set; }
    public float? PowerWatts { get; set; }
    public float? TemperatureCelsius { get; set; }
}
