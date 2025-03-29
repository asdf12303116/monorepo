// See https://aka.ms/new-console-template for more information


// Console.WriteLine("Hello, World!");


using System.Net.Http.Json;
using System.Text.RegularExpressions;
using esp32_host_app;
using LibreHardwareMonitor.Hardware;
using RestSharp;
using RestSharp.Authenticators;
using DataFormat = esp32_host_app.DataFormat;
using Monitor = esp32_host_app.Monitor;


var cpuInfo = new CpuInfo();
cpuInfo.GetCpuInfo(out var coreCount);
// test.Monitor();
var updateVisitor = new UpdateVisitor();
var computer = new Computer
{
    IsCpuEnabled = true,
    IsGpuEnabled = true,
    IsMemoryEnabled = true,
    IsMotherboardEnabled = true,
    IsControllerEnabled = true,
    IsNetworkEnabled = true,
    IsStorageEnabled = false
};
var monitor = new Monitor(computer, updateVisitor);

monitor.Init(out var sensors);


var options = new RestClientOptions("http://192.168.30.247:9780") {
    Timeout = TimeSpan.FromSeconds(10)
};
var client = new RestClient(options);


while (true)
{
    //更新传感器
    updateVisitor.VisitComputer(computer);
    
    var data =  DataFormat.GetJsonInfo(coreCount, sensors);

    HttpSend.send(client, data);
    
    
    // Task.WaitAll(sendTask);
    
    Thread.Sleep(1000);
}