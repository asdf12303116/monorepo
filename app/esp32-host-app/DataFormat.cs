using System.Text.Json;
using System.Text.Json.Nodes;
using System.Globalization;

namespace esp32_host_app;

public static partial class DataFormat
{
    public static JsonObject GetJsonInfo(CpuCoreCount coreCount, HardwareSnapshot snapshot, int fps)
    {
        var json = new JsonObject();
        var hasECore = coreCount.EfficientCore > 0;

        json["e_core_count"] = coreCount.EfficientCore;
        json["p_core_count"] = coreCount.PerformanceCore;
        json["logical_cpu_count"] = coreCount.LogicalCpu;
        json["physical_cpu_count"] = coreCount.PhysicalCpu;

        var basicData = new Data
        {
            cpu_avg_usage = snapshot.CpuAverageUsage,
            cpu_temp = snapshot.CpuTemperature,
            cpu_tdp = snapshot.CpuPower,
            gpu_core_freq = FormatString(snapshot.Gpu.CoreFrequencyMHz),
            gpu_core_usage_number = FormatString(snapshot.Gpu.CoreUsagePercent),
            gpu_core_volt = FormatString(snapshot.Gpu.CoreVoltageMillivolts, "0"),
            gpu_limit_heat = snapshot.Gpu.LimitHeat,
            gpu_limit_power = snapshot.Gpu.LimitPower,
            gpu_mem_freq = FormatString(snapshot.Gpu.MemoryFrequencyMHz),
            gpu_mem_usage_number = FormatString(snapshot.Gpu.MemoryUsedMiB),
            gpu_mem_usage_rate = FormatString(snapshot.Gpu.MemoryUsagePercent),
            gpu_tdp = FormatString(snapshot.Gpu.PowerWatts),
            gpu_temp = FormatString(snapshot.Gpu.TemperatureCelsius),
            mem_usage_number = FormatString(snapshot.Memory.UsedGigabytes),
            mem_usage_rate = FormatString(snapshot.Memory.UsagePercent)
        };

        var cpuFreqValues = snapshot.CpuCores
            .Where(core => core.FrequencyMHz.HasValue)
            .Select(core => core.FrequencyMHz!.Value)
            .ToList();

        if (cpuFreqValues.Count > 0)
        {
            basicData.cpu_freq = cpuFreqValues.Average();
        }

        if (hasECore)
        {
            var eCoreFrequencies = snapshot.CpuCores
                .Where(core => core.IsEfficientCore && core.FrequencyMHz.HasValue)
                .Select(core => core.FrequencyMHz!.Value)
                .ToList();
            if (eCoreFrequencies.Count > 0)
            {
                basicData.e_core_freq = eCoreFrequencies.Average();
            }

            var pCoreFrequencies = snapshot.CpuCores
                .Where(core => !core.IsEfficientCore && core.FrequencyMHz.HasValue)
                .Select(core => core.FrequencyMHz!.Value)
                .ToList();
            if (pCoreFrequencies.Count > 0)
            {
                basicData.p_core_freq = pCoreFrequencies.Average();
            }
        }

        var fpsValue = fps.ToString(CultureInfo.InvariantCulture);
        basicData.fps = fpsValue;
        basicData.present_mon_fps = fpsValue;
        basicData.rtss_fps = "0";

        var cpuFreqData = new JsonObject();
        var cpuUsageData = new JsonObject();
        foreach (var core in snapshot.CpuCores.OrderBy(x => x.CoreId))
        {
            cpuFreqData[$"cpu{core.CoreId}_freq"] = core.FrequencyMHz;
            cpuUsageData[$"cpu{core.CoreId}_usage_rate"] = core.UsagePercent;
        }

        json["cpu_freq_data"] = cpuFreqData;
        json["cpu_usage_data"] = cpuUsageData;
        json["data"] = JsonSerializer.SerializeToNode(basicData);

        return json;
    }

    private static string FormatString(float? value, string fallback = "0")
    {
        return value?.ToString(CultureInfo.InvariantCulture) ?? fallback;
    }
}
