using System.Collections;
using LibreHardwareMonitor.Hardware;

namespace esp32_host_app;

public class UpdateVisitor : IVisitor
{
    public void VisitComputer(IComputer computer)
    {
        computer.Traverse(this);
    }
    public void VisitHardware(IHardware hardware)
    {
        hardware.Update();
        foreach (IHardware subHardware in hardware.SubHardware) subHardware.Accept(this);
    }
    public void VisitSensor(ISensor sensor) { }
    public void VisitParameter(IParameter parameter) { }
}

public struct ReadSensor
{
    public CpuSensor CpuSensor;
    public MemorySensor MemorySensor;
    public GpuSensor GpuSensor;

}

public struct CpuSensor
{
    public String CpuName;
    public ISensor CpuVoltage;
    public ISensor CpuPower;
    public List<CpuClockUsage> CpuClockUsage;
    public ISensor CpuTotalUsage;
    public ISensor CpuTemperature; 
}

public struct CpuClockUsage
{
    public int CoreId;
    public ISensor CpuClock;
    public ISensor CpuLoad;
}

public struct MemorySensor
{
    public ISensor MemoryUsed;
    public ISensor MemoryAvailable;
    public ISensor MemoryLoad;
}
public struct GpuSensor
{

    public ISensor GpuPower;
    public ISensor GpuClock;
    public ISensor GpuLoad;
    public ISensor GpuTemperature;
    public ISensor GpuMemoryClock;
    public ISensor GpuMemoryUsed;
    public ISensor GpuMemoryLoad;
}

public class Monitor(Computer computer, UpdateVisitor updateVisitor)
{
    public Computer Computer { get; set; } = computer;
    public UpdateVisitor UpdateVisitor { get; set; } = updateVisitor;


    public void Init(out ReadSensor sensors)
    {
        
        computer.Open();
        computer.Accept(updateVisitor);

        sensors = new ReadSensor();
        
        var cpuSensor = new CpuSensor();
        var memorySensor = new MemorySensor();


        // 处理传感器数据
        
        // 获取硬件
        var motherboard = computer.Hardware.First(a => a.HardwareType == HardwareType.Motherboard);
        var superIo = motherboard.SubHardware.First(a => a.HardwareType == HardwareType.SuperIO);
        var cpu = computer.Hardware.First(a => a.HardwareType == HardwareType.Cpu);
        var mem = computer.Hardware.First(a => a.HardwareType == HardwareType.Memory);
        var intelGpu = computer.Hardware.FirstOrDefault(a => a.HardwareType == HardwareType.GpuIntel);
        var nvidiaGpu = computer.Hardware.FirstOrDefault(a => a.HardwareType == HardwareType.GpuNvidia);
        var amdGpu = computer.Hardware.FirstOrDefault(a => a.HardwareType == HardwareType.GpuAmd);

       // 获取CPU Sensor 信息

        // 判断CPU 型号
        cpuSensor.CpuName = cpu.Name;

        // CPU 电压
        var cpuVoltageSensor = cpu.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Voltage);

        if (cpuVoltageSensor != null)
        {
            cpuSensor.CpuVoltage = cpuVoltageSensor;
        }
        // CPU 功耗
        var cpuPowerSensor = cpu.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Power);
        if (cpuPowerSensor != null)
        {
            cpuSensor.CpuPower = cpuPowerSensor;
        }
        //  CPU 频率
        var cpuClockSensor = cpu.Sensors.Where(s => 
                s.SensorType == SensorType.Clock && s.Name.ToLower().Contains("cpu")).
            ToDictionary(s=>s.Name.Split("#")[1].Trim(), s=>s);
        
        // cpu 使用率
        var cpuLoadSensor = cpu.Sensors.Where(s => s.SensorType == SensorType.Load);
        var cpuTotalUsage = cpuLoadSensor.FirstOrDefault(s => s.Name.ToLower().Contains("total"));
        var cpuCoreUsage = cpuLoadSensor.Where(s => !s.Name.Contains("Thread #2") && s.Name.Contains("Core #")).
            ToDictionary(s=>s.Name.Replace("Thread #1","").Split("#")[1].Trim(), s=>s);
        if (cpuTotalUsage != null)
        {
            cpuSensor.CpuTotalUsage = cpuTotalUsage;
        }
        List<CpuClockUsage> cpuClockUsage = new List<CpuClockUsage>();
        for (var i = 1; i <= cpuClockSensor.Count; i++)
        {
           cpuClockUsage.Add(new CpuClockUsage
           {
                CoreId = i-1,
                CpuClock = cpuClockSensor[i.ToString()],
                CpuLoad = cpuCoreUsage[i.ToString()]
           }); 
        }
        cpuSensor.CpuClockUsage = cpuClockUsage;
        // cpu 温度
        var cpuTemperatureSensor = cpu.Sensors.FirstOrDefault(s => 
            s.SensorType == SensorType.Temperature && s.Name.ToLower().Contains("package"));
        if (cpuTemperatureSensor != null)
        {
            cpuSensor.CpuTemperature = cpuTemperatureSensor;
        }
        sensors.CpuSensor = cpuSensor;
        
        // 处理内存数据
        
        //内存容量
        memorySensor.MemoryUsed = mem.Sensors.FirstOrDefault(s => s.Name.ToLower().Contains("memory used"));
        memorySensor.MemoryAvailable = mem.Sensors.FirstOrDefault(s => s.Name.ToLower().Contains("memory available"));
        
        //内存负载
        memorySensor.MemoryLoad = mem.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Load && s.Name.ToLower().Contains("memory"));
        
        sensors.MemorySensor = memorySensor;
        
        // GPU信息
        var gpuSensor = nvidiaGpu??amdGpu;
        
        var gpuSensorInfo = new GpuSensor();

        if (gpuSensor.HardwareType == HardwareType.GpuNvidia)
        {
            gpuSensorInfo.GpuPower = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Power);
            gpuSensorInfo.GpuClock = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Clock && s.Name.ToLower().Contains("core"));
            gpuSensorInfo.GpuLoad = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Load && s.Name.ToLower().Contains("gpu"));
            gpuSensorInfo.GpuTemperature = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Temperature && s.Name.ToLower().Contains("core"));
            gpuSensorInfo.GpuMemoryClock = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Clock && s.Name.ToLower().Contains("memory"));
            gpuSensorInfo.GpuMemoryUsed = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.SmallData && s.Name.Equals("GPU Memory Used"));
            gpuSensorInfo.GpuMemoryLoad = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Load && s.Name.Equals("GPU Memory"));

        } else if (gpuSensor.HardwareType == HardwareType.GpuAmd)
        {
            gpuSensorInfo.GpuPower = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Power);
            gpuSensorInfo.GpuClock = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Clock && s.Name.ToLower().Contains("core"));
            gpuSensorInfo.GpuLoad = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Load && s.Name.ToLower().Contains("gpu"));
            gpuSensorInfo.GpuTemperature = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Temperature && s.Name.ToLower().Contains("core"));
            gpuSensorInfo.GpuMemoryClock = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Clock && s.Name.ToLower().Contains("memory"));
            gpuSensorInfo.GpuMemoryUsed = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.SmallData && s.Name.Equals("GPU Memory Used"));
            gpuSensorInfo.GpuMemoryLoad = gpuSensor.Sensors.FirstOrDefault(s => s.SensorType == SensorType.Load && s.Name.Equals("GPU Memory"));

        }
        

        sensors.GpuSensor = gpuSensorInfo;



        // computer.Close();
    }
}