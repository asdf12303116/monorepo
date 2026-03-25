using esp32_host_app;

var cpuInfo = new CpuInfo();
cpuInfo.GetCpuInfo(out var coreCount);
var cancellationSource = new CancellationTokenSource();

Console.CancelKeyPress += (_, eventArgs) =>
{
    eventArgs.Cancel = true;
    cancellationSource.Cancel();
};

var runner = AppRunnerFactory.Create();
await runner.RunAsync(coreCount, cancellationSource.Token);
