using System.Runtime.InteropServices;
using System.Globalization;

namespace esp32_host_app;

// 定义 SYSTEM_CPU_SET_INFORMATION 结构
[StructLayout(LayoutKind.Sequential)]
public struct SYSTEM_CPU_SET_INFORMATION
{
    public uint Size; // 结构的大小
    public CPU_SET_INFORMATION_TYPE Type; // 信息类型
    public CPU_SET_INFORMATION_UNION Info; // 信息内容
}

// 定义 CPU_SET_INFORMATION_TYPE 枚举
public enum CPU_SET_INFORMATION_TYPE
{
    CpuSetInformation = 0x0
}

// 定义联合体，用于表示 CPU 集信息
[StructLayout(LayoutKind.Explicit)]
public struct CPU_SET_INFORMATION_UNION
{
    [FieldOffset(0)] public CPU_SET CpuSet;
}

// 定义 CPU_SET 结构
[StructLayout(LayoutKind.Sequential)]
public struct CPU_SET
{
    public uint Id;
    public ushort Group;
    public byte LogicalProcessorIndex;
    public byte CoreIndex;
    public byte LastLevelCacheIndex;
    public byte NumaNodeIndex;
    public byte EfficiencyClass;
    public byte AllFlags;
    public ulong AllocationTag;
}

public struct CoreRange
{
    public int Start;
    public int End;
    public static CoreRange Empty => new() { Start = -1, End = -1 };
}

public struct CpuCoreCount
{
    public int TotalCore;
    public int PhysicalCpu;
    public int LogicalCpu;
    public int PerformanceCore;
    public int EfficientCore;
    public CoreRange SMTCoreRange;
    public CoreRange EfficientCoreRange;
}

public class CoreInfo
{
    public uint CoreIndex;
    public uint EfficiencyClass;
    public uint Id;
    public uint LogicalProcessorIndex;
}

public class CpuInfo
{
    [DllImport("kernel32.dll", SetLastError = true)]
    private static extern bool GetSystemCpuSetInformation(
        IntPtr Information, // 输出缓冲区
        uint BufferLength, // 缓冲区大小
        out uint ReturnedLength, // 实际返回的字节数
        IntPtr ProcessHandle, // 要查询的进程句柄，NULL 表示当前系统
        uint Flags // 保留，必须为 0
    );

    public void GetCpuInfo(out CpuCoreCount coreCount)
    {
        coreCount = new CpuCoreCount();

        if (RuntimeInformation.OSDescription.Contains("Windows"))
        {
            // 尝试调用 GetSystemCpuSetInformation
            var buffer = IntPtr.Zero;
            uint returnedLength;

            try
            {
                // 第一次调用以获取所需缓冲区大小
                if (!GetSystemCpuSetInformation(IntPtr.Zero, 0, out returnedLength, IntPtr.Zero, 0))
                {
                    var error = Marshal.GetLastWin32Error();
                    if (error != 122) // ERROR_INSUFFICIENT_BUFFER
                    {
                        Console.WriteLine($"第一次调用失败，错误代码: {error}");
                        return;
                    }
                }

                // 分配缓冲区
                buffer = Marshal.AllocHGlobal((int)returnedLength);

                // 第二次调用以获取 CPU 集信息
                if (GetSystemCpuSetInformation(buffer, returnedLength, out returnedLength, IntPtr.Zero, 0))
                {
                    var coreInfoList = new List<CoreInfo>();
                    // 遍历缓冲区中的信息
                    var current = buffer;
                    while (current < buffer + returnedLength)
                    {
                        var info = Marshal.PtrToStructure<SYSTEM_CPU_SET_INFORMATION>(current);

                        // 打印 CPU 信息
                        var coreInfo = new CoreInfo
                        {
                            Id = info.Info.CpuSet.Id,
                            CoreIndex = info.Info.CpuSet.CoreIndex,
                            LogicalProcessorIndex = info.Info.CpuSet.LogicalProcessorIndex,
                            EfficiencyClass = info.Info.CpuSet.EfficiencyClass
                        };
                        coreInfoList.Add(coreInfo);

                        // 移动指针到下一个结构
                        current = IntPtr.Add(current, (int)info.Size);
                    }

                    coreCount = new CpuCoreCount();

                    // 基础统计 - 不需要转换为List
                    var coreInfoArray = coreInfoList.ToArray(); // 只转换一次，避免多次枚举
                    var physicalGroups = coreInfoArray.GroupBy(c => c.CoreIndex);

                    coreCount.TotalCore = coreInfoArray.Length;
                    coreCount.PhysicalCpu = physicalGroups.Count();
                    coreCount.LogicalCpu = coreInfoArray.Length;

                    // 超线程范围计算 - 避免额外的ToList
                    var smtCores = coreInfoArray.Where(c =>
                        physicalGroups.First(g => g.Key == c.CoreIndex).Count() > 1);

                    if (smtCores.Any())
                        coreCount.SMTCoreRange = new CoreRange
                        {
                            Start = (int)smtCores.Min(c => c.LogicalProcessorIndex) + 1,
                            End = smtCores.Select(s => s.CoreIndex).Distinct().Count()
                        };
                    else
                        coreCount.SMTCoreRange = CoreRange.Empty;

                    // 能效核心分类 - 简化逻辑
                    var efficiencyGroups = coreInfoArray.GroupBy(c => c.EfficiencyClass).ToArray();

                    if (efficiencyGroups.Length > 1)
                    {
                        // 找出性能核心组(最高效率类)和能效核心组(最低效率类)
                        var maxEfficiencyClass = efficiencyGroups.Max(g => g.Key);
                        var minEfficiencyClass = efficiencyGroups.Min(g => g.Key);

                        var performanceCores = coreInfoArray.Where(c => c.EfficiencyClass == maxEfficiencyClass);
                        var efficientCores = coreInfoArray.Where(c => c.EfficiencyClass == minEfficiencyClass);

                        // 性能核心统计
                        coreCount.PerformanceCore = performanceCores
                            .Select(c => c.CoreIndex)
                            .Distinct()
                            .Count();

                        // 能效核心统计
                        coreCount.EfficientCore = efficientCores
                            .Select(c => c.CoreIndex)
                            .Distinct()
                            .Count();

                        // 能效核心范围
                        if (efficientCores.Any())
                        {
                            var isEfficientCoreBefore = efficientCores.Min(c => c.LogicalProcessorIndex) == 0;
                            if (isEfficientCoreBefore)
                                coreCount.EfficientCoreRange = new CoreRange
                                {
                                    Start = (int)efficientCores.Min(c => c.LogicalProcessorIndex),
                                    End = (int)efficientCores.Max(c => c.LogicalProcessorIndex)
                                };
                            else
                                coreCount.EfficientCoreRange = new CoreRange
                                {
                                    Start = (int)efficientCores.Min(c => c.LogicalProcessorIndex) -
                                            coreCount.PerformanceCore,
                                    End = (int)efficientCores.Max(c => c.LogicalProcessorIndex) -
                                          coreCount.PerformanceCore
                                };
                        }
                        else
                        {
                            coreCount.EfficientCoreRange = CoreRange.Empty;
                        }
                    }
                    else
                    {
                        // 只有一种核心类型，全部算作性能核心
                        coreCount.PerformanceCore = coreCount.PhysicalCpu;
                        coreCount.EfficientCore = 0;
                        coreCount.EfficientCoreRange = CoreRange.Empty;
                    }
                }
                else
                {
                    var error = Marshal.GetLastWin32Error();
                    Console.WriteLine($"第二次调用失败，错误代码: {error}");
                }
            }
            finally
            {
                // 释放分配的缓冲区
                if (buffer != IntPtr.Zero) Marshal.FreeHGlobal(buffer);
            }
        }
        else if (RuntimeInformation.OSDescription.Contains("Linux"))
        {
            PopulateLinuxCpuInfo(out coreCount);
        }
    }

    private static void PopulateLinuxCpuInfo(out CpuCoreCount coreCount)
    {
        coreCount = new CpuCoreCount();
        const string cpuSysPath = "/sys/devices/system/cpu";

        var logicalCpuIds = Directory.Exists(cpuSysPath)
            ? Directory.EnumerateDirectories(cpuSysPath, "cpu*")
                .Select(Path.GetFileName)
                .Where(name => name is not null && name.Length > 3 && name[3..].All(char.IsDigit))
                .Select(name => int.Parse(name![3..], CultureInfo.InvariantCulture))
                .OrderBy(id => id)
                .ToList()
            : [];

        coreCount.LogicalCpu = logicalCpuIds.Count;
        coreCount.TotalCore = logicalCpuIds.Count;

        var physicalCoreKeys = new HashSet<string>(StringComparer.Ordinal);
        foreach (var cpuId in logicalCpuIds)
        {
            var coreIdPath = Path.Combine(cpuSysPath, $"cpu{cpuId}", "topology", "core_id");
            var packageIdPath = Path.Combine(cpuSysPath, $"cpu{cpuId}", "topology", "physical_package_id");

            var coreId = File.Exists(coreIdPath) ? File.ReadAllText(coreIdPath).Trim() : cpuId.ToString(CultureInfo.InvariantCulture);
            var packageId = File.Exists(packageIdPath) ? File.ReadAllText(packageIdPath).Trim() : "0";
            physicalCoreKeys.Add($"{packageId}:{coreId}");
        }

        coreCount.PhysicalCpu = physicalCoreKeys.Count > 0 ? physicalCoreKeys.Count : coreCount.LogicalCpu;

        var eCoreCpuIds = CpuSetParser.Parse(ReadAllTextOrEmpty("/sys/devices/cpu_atom/cpus"));
        var pCoreCpuIds = CpuSetParser.Parse(ReadAllTextOrEmpty("/sys/devices/cpu_core/cpus"));

        if (eCoreCpuIds.Count > 0 && pCoreCpuIds.Count > 0)
        {
            coreCount.EfficientCore = CountPhysicalCores(cpuSysPath, eCoreCpuIds);
            coreCount.PerformanceCore = CountPhysicalCores(cpuSysPath, pCoreCpuIds);
            coreCount.EfficientCoreRange = BuildRange(logicalCpuIds, eCoreCpuIds);
            coreCount.SMTCoreRange = BuildSmtRange(cpuSysPath, logicalCpuIds);
        }
        else
        {
            coreCount.EfficientCore = 0;
            coreCount.PerformanceCore = coreCount.PhysicalCpu;
            coreCount.EfficientCoreRange = CoreRange.Empty;
            coreCount.SMTCoreRange = BuildSmtRange(cpuSysPath, logicalCpuIds);
        }
    }

    private static int CountPhysicalCores(string cpuSysPath, HashSet<int> logicalCpuIds)
    {
        var physicalCoreKeys = new HashSet<string>(StringComparer.Ordinal);
        foreach (var cpuId in logicalCpuIds)
        {
            var coreIdPath = Path.Combine(cpuSysPath, $"cpu{cpuId}", "topology", "core_id");
            var packageIdPath = Path.Combine(cpuSysPath, $"cpu{cpuId}", "topology", "physical_package_id");

            var coreId = File.Exists(coreIdPath) ? File.ReadAllText(coreIdPath).Trim() : cpuId.ToString(CultureInfo.InvariantCulture);
            var packageId = File.Exists(packageIdPath) ? File.ReadAllText(packageIdPath).Trim() : "0";
            physicalCoreKeys.Add($"{packageId}:{coreId}");
        }

        return physicalCoreKeys.Count;
    }

    private static CoreRange BuildRange(List<int> logicalCpuIds, HashSet<int> targetCpuIds)
    {
        var indexes = logicalCpuIds
            .Select((cpuId, index) => new { cpuId, index })
            .Where(item => targetCpuIds.Contains(item.cpuId))
            .Select(item => item.index)
            .ToList();

        return indexes.Count == 0
            ? CoreRange.Empty
            : new CoreRange { Start = indexes.Min(), End = indexes.Max() };
    }

    private static CoreRange BuildSmtRange(string cpuSysPath, List<int> logicalCpuIds)
    {
        var siblingCounts = new List<int>();
        foreach (var cpuId in logicalCpuIds)
        {
            var siblingsPath = Path.Combine(cpuSysPath, $"cpu{cpuId}", "topology", "thread_siblings_list");
            if (!File.Exists(siblingsPath))
            {
                continue;
            }

            var siblings = CpuSetParser.Parse(File.ReadAllText(siblingsPath));
            siblingCounts.Add(siblings.Count);
        }

        if (siblingCounts.Count == 0 || siblingCounts.Max() <= 1)
        {
            return CoreRange.Empty;
        }

        return new CoreRange { Start = 1, End = siblingCounts.Count(count => count > 1) };
    }

    private static string ReadAllTextOrEmpty(string path)
    {
        return File.Exists(path) ? File.ReadAllText(path) : string.Empty;
    }
}
