# Repository Guidelines

## Project Structure & Module Organization
本仓库是基于 PlatformIO 的 ESP32-S3 监控固件工程。`src/` 存放核心逻辑：`main.cpp` 为入口，`mqtt.cpp` 处理 MQTT 接入，`WebServer.cpp` 提供本地 HTTP 接口，`update.cpp` 负责数据解析和界面刷新。`include/` 放公共头文件与全局声明（如 `global.h`、`Config.h`、`update.h`）。`lib/ui/` 是 SquareLine Studio 生成的 LVGL 代码，建议在设计工具修改后重新导出。`config/` 存放板级配置，`ssl_certs/` 存放证书资源；依赖与构建参数统一维护在 `platformio.ini`。

## Code Logic & Data Flow
启动阶段在 `setup()` 中按顺序执行：串口初始化、`initLcd_lvgl()`、`ui_init()`、`setupWiFi()`、`init_mqtt()`，然后注册两个定时器：`ticker_wifi`（60 秒重连检查）和 `ticker_lcd`（数据超时检查）。

运行阶段在 `loop()` 中处理两类任务：一是读取串口 JSON，`read_data()` 成功后调用 `update_data(..., SERIAL_UPDATE)`；二是执行 `lv_timer_handler()`，并每 500ms 调用 `update_show_data()` 刷新 UI。

网络数据通过 MQTT 回调 `onTopic()` 进入，反序列化成功后调用 `update_data(..., NETWORK_UPDATE)`。`update_data()` 会更新 CPU/GPU/内存状态、设置 `sensor_data_updated`，并调用 `lcd_check()` 与 `data_timeout_set()` 维持亮屏与超时计时。`check_data_timeout()` 在断流超过 `DATA_TIMEOUT_SEC` 时熄屏。

### JSON Contract (`update_data`)
最小字段集合：顶层 `logical_cpu_count`、`physical_cpu_count`、`e_core_count`、`p_core_count`、`cpu_usage_data.cpu{i}_usage_rate`；`data` 节点包含 `fps`、`cpu_freq`、`p_core_freq`、`e_core_freq`、`cpu_avg_usage`、`cpu_tdp`、`cpu_temp`、`mem_usage_number`、`mem_usage_rate`、`gpu_core_freq`、`gpu_core_usage_number`、`gpu_core_volt`、`gpu_mem_freq`、`gpu_mem_usage_number`、`gpu_mem_usage_rate`、`gpu_tdp`、`gpu_temp`、`gpu_limit_heat`、`gpu_limit_power`。

## Build, Test, and Development Commands
- `pio run -e esp32dev`：编译固件。
- `pio run -e esp32dev -t upload`：烧录到 `esp32-s3-devkitc-1`。
- `pio device monitor -b 115200`：查看串口日志。
- `pio run -e esp32dev -t clean`：清理构建缓存。
- `pio test -e esp32dev`：运行测试（新增 `test/` 后生效）。

## Coding Style & Naming Conventions
C/C++ 使用 4 空格缩进并保留 include guard。模块建议使用 `xxx.cpp`/`xxx.h` 成对命名。UI 变量保持 `ui_*` 前缀，避免手动重命名生成符号。新增配置优先放在 `include/` 或 `platformio.ini`，避免散落硬编码。修改 `lib/ui/` 生成文件时，请在 PR 说明是否来自重新导出。

## Testing Guidelines
当前仓库未内置默认自动化测试。提交前至少完成：1) `pio run` 编译通过；2) 串口 JSON 可触发界面刷新；3) MQTT 消息可更新数据；4) 超时熄屏与恢复显示正常。新增测试建议放在 `test/`，命名为 `test_<module>.cpp`。

逻辑回归检查：
- 串口或 MQTT 任一路径输入合法 JSON 后，`update_data()` 能同步更新数值与条形图。
- 停止输入超过 `DATA_TIMEOUT_SEC` 后触发熄屏，恢复输入后自动亮屏并继续刷新。

## Commit & Pull Request Guidelines
提交信息遵循 Conventional Commits，建议沿用历史作用域，如 `feat(mcu/esp32-monitor): ...`、`build(mcu/esp32-monitor): ...`。PR 需包含变更说明、验证步骤和目标硬件信息；涉及 UI 变更请附截图。不要提交真实 Wi-Fi、MQTT 凭据或私有地址，示例配置请使用占位值。
