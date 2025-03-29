using System.Text.Json.Nodes;

namespace esp32_host_app;

public class SendData
{
    public JsonObject cpu_freq_data { get; set; }
    public JsonObject cpu_usage_data { get; set; }
    public Data data { get; set; }
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
        
        public string gpu_core_freq { get; set; }
        public string gpu_core_usage_number { get; set; }
        public string gpu_core_volt { get; set; }
        public string gpu_limit_heat { get; set; }
        public string gpu_limit_power { get; set; }
        public string gpu_mem_freq { get; set; }
        public string gpu_mem_usage_number { get; set; }
        public string gpu_mem_usage_rate { get; set; }
        public string gpu_tdp { get; set; }
        public string gpu_temp { get; set; }
        public string mem_usage_number { get; set; }
        public string mem_usage_rate { get; set; }
        
        public string fps { get; set; }
        public string present_mon_fps { get; set; }
        public string rtss_fps { get; set; }
    }