using System.Text.Json;
using System.Text.Json.Nodes;

namespace esp32_host_app;

public static class DataFormat
{
    public static JsonObject GetJsonInfo(CpuCoreCount coreCount, ReadSensor sensor)
    {
        var json = new JsonObject();
        var hasECore = coreCount.EfficientCore > 0;
        // 外层基础数据
        json["e_core_count"] = coreCount.EfficientCore;
        json["p_core_count"] = coreCount.PerformanceCore;
        json["logical_cpu_count"] = coreCount.LogicalCpu;
        json["physical_cpu_count"] = coreCount.PhysicalCpu;
        
        var basicData = new Data();
        
        // cpu数据
        basicData.cpu_avg_usage = sensor.CpuSensor.CpuTotalUsage.Value;
        if (hasECore)
        {
            basicData.e_core_freq = sensor.CpuSensor.CpuClockUsage
                .Where(s => s.CoreId >= coreCount.EfficientCoreRange.Start &&
                            s.CoreId <= coreCount.EfficientCoreRange.End)
                .Select(s => s.CpuClock.Value).Average();
            basicData.p_core_freq = sensor.CpuSensor.CpuClockUsage
                .Where(x => x.CoreId < 6 || x.CoreId > 13)
                .Select(s => s.CpuClock.Value).Average();
            basicData.cpu_freq = sensor.CpuSensor.CpuClockUsage.Select(x=>x.CpuClock.Value).Average();
        }
        else
        {
            basicData.cpu_freq = sensor.CpuSensor.CpuClockUsage.Select(x=>x.CpuClock.Value).Average();
        }
        basicData.cpu_freq = sensor.CpuSensor.CpuPower.Value;
        basicData.cpu_temp = sensor.CpuSensor.CpuTemperature.Value;
        basicData.cpu_tdp = sensor.CpuSensor.CpuPower.Value;
        
        //gpu 数据
        basicData.gpu_core_freq = sensor.GpuSensor.GpuClock.Value.ToString();
        basicData.gpu_core_usage_number = sensor.GpuSensor.GpuLoad.Value.ToString();
        basicData.gpu_core_volt = "";
        basicData.gpu_limit_heat = "";
        basicData.gpu_limit_power = "";
        basicData.gpu_mem_freq = sensor.GpuSensor.GpuMemoryClock.Value.ToString();
        basicData.gpu_mem_usage_number = sensor.GpuSensor.GpuMemoryUsed.Value.ToString();
        basicData.gpu_mem_usage_rate = sensor.GpuSensor.GpuMemoryLoad.Value.ToString();
        basicData.gpu_tdp = sensor.GpuSensor.GpuPower.Value.ToString();
        basicData.gpu_temp = sensor.GpuSensor.GpuTemperature.Value.ToString();
        
        // mem 数据
        basicData.mem_usage_number = sensor.MemorySensor.MemoryUsed.Value.ToString();
        basicData.mem_usage_rate = sensor.MemorySensor.MemoryLoad.Value.ToString();
        
        // fps 数据
        basicData.fps = "";
        basicData.present_mon_fps = "";
        basicData.rtss_fps="";
        
        // cpu_freq_data cpu_usage_data
        var cpu_freq_data = new JsonObject();
        var cpu_usage_data = new JsonObject();
        foreach (var cpuClockUsage in sensor.CpuSensor.CpuClockUsage)
        {
            cpu_freq_data[$"cpu{cpuClockUsage.CoreId}_freq"] = cpuClockUsage.CpuClock.Value;
            cpu_usage_data[$"cpu{cpuClockUsage.CoreId}_usage_rate"] = cpuClockUsage.CpuLoad.Value;
        }
        
        json["cpu_freq_data"] = cpu_freq_data;
        json["cpu_usage_data"] = cpu_usage_data;
        json["data"] = JsonSerializer.SerializeToNode(basicData);
        
        return json;
    }
    
}