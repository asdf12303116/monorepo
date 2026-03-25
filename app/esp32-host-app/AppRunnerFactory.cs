namespace esp32_host_app;

internal static class AppRunnerFactory
{
    public static IAppRunner Create()
    {
        if (OperatingSystem.IsLinux())
        {
            return new LinuxAppRunner();
        }

        if (OperatingSystem.IsWindows())
        {
            var runnerType = Type.GetType("esp32_host_app.WindowsAppRunner, esp32-host-app");
            if (runnerType == null)
            {
                throw new PlatformNotSupportedException("当前构建未包含 Windows 采集实现。");
            }

            return (IAppRunner)Activator.CreateInstance(runnerType)!;
        }

        throw new PlatformNotSupportedException("当前仅支持 Windows 和 Linux。");
    }
}
