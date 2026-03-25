namespace esp32_host_app;

internal interface IAppRunner
{
    Task RunAsync(CpuCoreCount coreCount, CancellationToken cancellationToken);
}
