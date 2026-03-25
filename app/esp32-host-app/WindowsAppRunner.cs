using LibreHardwareMonitor.Hardware;

namespace esp32_host_app;

internal sealed class WindowsAppRunner : IAppRunner
{
    public async Task RunAsync(CpuCoreCount coreCount, CancellationToken cancellationToken)
    {
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
        var mqttClient = new MqttSend();

        try
        {
            while (!cancellationToken.IsCancellationRequested)
            {
                updateVisitor.VisitComputer(computer);
                var fpsSnapshot = fpsCollector.GetSnapshot();
                var data = DataFormat.GetJsonInfo(coreCount, sensors, fpsSnapshot.Value);

                await mqttClient.SendAsync(data);

                try
                {
                    await Task.Delay(1000, cancellationToken);
                }
                catch (TaskCanceledException)
                {
                    break;
                }
            }
        }
        finally
        {
            computer.Close();
        }
    }
}
