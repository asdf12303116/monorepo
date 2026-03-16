# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

ESP32 Host App 是一个 .NET 8 控制台应用，运行于 PC 端（Windows/Linux），通过 LibreHardwareMonitor 采集 CPU、GPU、内存等硬件传感器数据，以 1 秒间隔通过 MQTT 发送 JSON 格式数据到 ESP32 设备进行显示。属于 monorepo 的一部分，配套 ESP32 固件位于 `../../mcu/esp32-monitor/`。

## 常用命令

```powershell
dotnet restore                                 # 恢复 NuGet 依赖
dotnet build -c Debug                          # 编译（提交前必须通过）
dotnet run --project .\esp32-host-app.csproj   # 运行（需管理员权限读取传感器）
dotnet publish -c Release -o .\out             # 发布到 out/
```

当前无独立测试项目，验证方式：`dotnet build` 通过 + `dotnet run` 完成至少一次采集发送且无未处理异常。

## 架构与数据流

```
CpuInfo (CPU 拓扑: P/E-core 检测)
        ↓
Program.cs (入口 + 主循环)
        ↓
Monitor.cs (LibreHardwareMonitor Visitor 模式采集传感器)
        ↓
DataFormat.cs (组装 JSON: 核心频率/负载/温度/功耗)
        ↓
MqttSend.cs (MQTTnet 客户端发布到 broker)
```

### 关键设计点

- **CPU P/E-core 识别**：`CpuInfo.cs` 在 Windows 上通过 `GetSystemCpuSetInformation` 内核 API（P/Invoke）区分性能核与能效核；Linux 上通过 `/sys/devices/cpu_atom/` 和 `/sys/devices/cpu_core/` 读取（当前为硬编码值）。
- **传感器绑定**：`Monitor.Init()` 在启动时一次性绑定所有 `ISensor` 引用到 `ReadSensor` 结构体，主循环仅调用 `VisitComputer` 刷新值，无需重新查找。
- **数据模型**：`SendData.cs` 中的 `Data` 类定义了 ESP32 端期望的 JSON 字段契约，GPU/内存/FPS 字段为 `string` 类型，CPU 字段为 `float?`。
- **MQTT 重连**：`MqttSend.SendAsync()` 在每次发送前检查连接状态，断线时自动重连。
- **已弃用路径**：`HttpSend.cs` 是旧的 HTTP 发送方式，已被 MQTT 替代，`Program.cs` 中相关代码已注释。

## 编码规范

- 4 空格缩进，UTF-8 编码，`nullable` 启用
- 类型/方法/属性用 `PascalCase`，局部变量/参数用 `camelCase`，异步方法以 `Async` 结尾
- 新逻辑按"采集 / 格式化 / 发送"职责拆分
- 网络调用必须处理异常与重连
- 禁止硬编码配置（broker、topic、凭据等），优先环境变量或本地配置文件（需 `.gitignore` 忽略）
- 禁止提交真实地址、账号密码、证书私钥或 token

## 提交规范

格式：`type(scope): summary`

示例：`feat(app/esp32-host-app): 新增mqtt支持`、`build(app/esp32-host-app): 调整构建参数`

## 依赖

| 包 | 用途 |
|---|------|
| LibreHardwareMonitorLib | 硬件传感器读取（CPU/GPU/内存/主板） |
| MQTTnet | MQTT 协议客户端 |
| RestSharp | HTTP 客户端（已弃用，保留兼容） |
