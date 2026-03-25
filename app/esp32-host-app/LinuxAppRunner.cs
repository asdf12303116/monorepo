namespace esp32_host_app;

internal sealed class LinuxAppRunner : IAppRunner
{
    public async Task RunAsync(CpuCoreCount coreCount, CancellationToken cancellationToken)
    {
        using var fpsCollector = new PresentMonFpsCollector();
        var mqttClient = new MqttSend();
        var hardwareMonitor = new LinuxHardwareMonitor();

        while (!cancellationToken.IsCancellationRequested)
        {
            try
            {
                var fpsSnapshot = fpsCollector.GetSnapshot();
                var snapshot = hardwareMonitor.Capture();
                var data = DataFormat.GetJsonInfo(coreCount, snapshot, fpsSnapshot.Value);

                await mqttClient.SendAsync(data);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Linux采集失败: {ex.Message}");
            }

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
}
