using System.Globalization;
using System.Text.Json;
using System.Text.Json.Nodes;

namespace esp32_host_app;

public static partial class DataFormat
{
    public static JsonObject GetJsonInfo(CpuCoreCount coreCount, ReadSensor sensor, int fps)
    {
        var json = new JsonObject();
        var hasECore = coreCount.EfficientCore > 0;
        json["e_core_count"] = coreCount.EfficientCore;
        json["p_core_count"] = coreCount.PerformanceCore;
        json["logical_cpu_count"] = coreCount.LogicalCpu;
        json["physical_cpu_count"] = coreCount.PhysicalCpu;

        var basicData = new Data();

        basicData.cpu_avg_usage = sensor.CpuSensor.CpuTotalUsage?.Value;
        if (hasECore)
        {
            var eCoreFrequencies = sensor.CpuSensor.CpuClockUsage
                .Where(s => s.CoreId >= coreCount.EfficientCoreRange.Start &&
                            s.CoreId <= coreCount.EfficientCoreRange.End)
                .Select(s => s.CpuClock.Value)
                .Where(value => value.HasValue)
                .Select(value => value!.Value)
                .ToList();
            if (eCoreFrequencies.Count > 0)
            {
                basicData.e_core_freq = eCoreFrequencies.Average();
            }

            var pCoreFrequencies = sensor.CpuSensor.CpuClockUsage
                .Where(x => x.CoreId < 6 || x.CoreId > 13)
                .Select(s => s.CpuClock.Value)
                .Where(value => value.HasValue)
                .Select(value => value!.Value)
                .ToList();
            if (pCoreFrequencies.Count > 0)
            {
                basicData.p_core_freq = pCoreFrequencies.Average();
            }
        }

        var cpuFrequencies = sensor.CpuSensor.CpuClockUsage
            .Select(x => x.CpuClock.Value)
            .Where(value => value.HasValue)
            .Select(value => value!.Value)
            .ToList();
        if (cpuFrequencies.Count > 0)
        {
            basicData.cpu_freq = cpuFrequencies.Average();
        }

        basicData.cpu_temp = sensor.CpuSensor.CpuTemperature?.Value;
        basicData.cpu_tdp = sensor.CpuSensor.CpuPower?.Value;

        basicData.gpu_core_freq = sensor.GpuSensor.GpuClock?.Value?.ToString() ?? string.Empty;
        basicData.gpu_core_usage_number = sensor.GpuSensor.GpuLoad?.Value?.ToString() ?? string.Empty;
        var gpuVoltageValue = sensor.GpuSensor.GpuVoltage?.Value;
        basicData.gpu_core_volt = gpuVoltageValue.HasValue && !float.IsNaN(gpuVoltageValue.Value)
            ? ((int)Math.Round(gpuVoltageValue.Value * 1000, MidpointRounding.AwayFromZero)).ToString()
            : "0";

        basicData.gpu_limit_heat = "";
        basicData.gpu_limit_power = "";
        basicData.gpu_mem_freq = sensor.GpuSensor.GpuMemoryClock?.Value?.ToString() ?? string.Empty;
        basicData.gpu_mem_usage_number = sensor.GpuSensor.GpuMemoryUsed?.Value?.ToString() ?? string.Empty;
        basicData.gpu_mem_usage_rate = sensor.GpuSensor.GpuMemoryLoad?.Value?.ToString() ?? string.Empty;
        basicData.gpu_tdp = sensor.GpuSensor.GpuPower?.Value.ToString() ?? "0";
        basicData.gpu_temp = sensor.GpuSensor.GpuTemperature?.Value?.ToString() ?? string.Empty;

        basicData.mem_usage_number = sensor.MemorySensor.MemoryUsed?.Value?.ToString() ?? string.Empty;
        basicData.mem_usage_rate = sensor.MemorySensor.MemoryLoad?.Value?.ToString() ?? string.Empty;

        var fpsValue = fps.ToString(CultureInfo.InvariantCulture);
        basicData.fps = fpsValue;
        basicData.present_mon_fps = fpsValue;
        basicData.rtss_fps = "0";

        var cpuFreqData = new JsonObject();
        var cpuUsageData = new JsonObject();
        foreach (var cpuClockUsage in sensor.CpuSensor.CpuClockUsage)
        {
            cpuFreqData[$"cpu{cpuClockUsage.CoreId}_freq"] = cpuClockUsage.CpuClock.Value;
            cpuUsageData[$"cpu{cpuClockUsage.CoreId}_usage_rate"] = cpuClockUsage.CpuLoad.Value;
        }

        json["cpu_freq_data"] = cpuFreqData;
        json["cpu_usage_data"] = cpuUsageData;
        json["data"] = JsonSerializer.SerializeToNode(basicData);
        return json;
    }
}
