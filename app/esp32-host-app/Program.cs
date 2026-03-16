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
using var fpsCollector = new PresentMonFpsCollector();
var isRunning = true;

Console.CancelKeyPress += (_, eventArgs) =>
{
    eventArgs.Cancel = true;
    isRunning = false;
};


// var options = new RestClientOptions("http://192.168.30.247:9780") {
//     Timeout = TimeSpan.FromSeconds(10)
// };
// var client = new RestClient(options);

var mqttClient = new MqttSend();


while (isRunning)
{
    //更新传感器
    updateVisitor.VisitComputer(computer);
    var fpsSnapshot = fpsCollector.GetSnapshot();
    // Console.WriteLine($"当前pid: {fpsSnapshot.ProcessId},当前fps: {fpsSnapshot.Value}");
    var data = DataFormat.GetJsonInfo(coreCount, sensors, fpsSnapshot.Value);

    // HttpSend.send(client, data);
    _ = mqttClient.SendAsync(data);
    
    
    // Task.WaitAll(sendTask);
    
    Thread.Sleep(1000);
}

computer.Close();
