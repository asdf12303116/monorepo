using System.Text.Json.Nodes;

namespace esp32_host_app;

public class SendData
{
    public JsonObject cpu_freq_data { get; set; } = new();
    public JsonObject cpu_usage_data { get; set; } = new();
    public Data data { get; set; } = new();
    public int e_core_count { get; set; }
    public int logical_cpu_count { get; set; }
    public int p_core_count { get; set; }
    public int physical_cpu_count { get; set; }
}


public class Data
    {
        public float? cpu_avg_usage { get; set; }
        public float? cpu_freq { get; set; }
        public float? e_core_freq { get; set; }
        public float? p_core_freq { get; set; }
        public float? cpu_tdp { get; set; }
        public float? cpu_temp { get; set; }
        
        public string gpu_core_freq { get; set; } = string.Empty;
        public string gpu_core_usage_number { get; set; } = string.Empty;
        public string gpu_core_volt { get; set; } = string.Empty;
        public string gpu_limit_heat { get; set; } = string.Empty;
        public string gpu_limit_power { get; set; } = string.Empty;
        public string gpu_mem_freq { get; set; } = string.Empty;
        public string gpu_mem_usage_number { get; set; } = string.Empty;
        public string gpu_mem_usage_rate { get; set; } = string.Empty;
        public string gpu_tdp { get; set; } = string.Empty;
        public string gpu_temp { get; set; } = string.Empty;
        public string mem_usage_number { get; set; } = string.Empty;
        public string mem_usage_rate { get; set; } = string.Empty;
        
        public string fps { get; set; } = string.Empty;
        public string present_mon_fps { get; set; } = string.Empty;
        public string rtss_fps { get; set; } = string.Empty;
    }
