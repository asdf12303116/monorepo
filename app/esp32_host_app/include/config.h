#ifndef CONFIG_H
#define CONFIG_H
#include <httplib.h>
#include <sensor.h>
#include <spdlog/spdlog.h>
#include <string>
const static std::string CONFIG_FILE = "./config.ini";
const static std::string BASIC_CONFIG = "ESP32";

struct ESP32_CONFIG{
  std::string serial_port;
  int serial_rate;
  std::string hostname;
  int port;
  std::string path;
  int interval;
  int fps_source;
  int mode;
};
constexpr int ESP32_CONFIG_LEN = 10;

const static std::string ESP32_CONFIG_DIC[ESP32_CONFIG_LEN]=
{
  "SerialPort",
  "SerialRate",
  "HostName",
  "Port",
  "Path",
  "Interval",
  "FpsSource",
  "Mode"
};

enum ESP32_CONFIG_INDEX {
  SERIAL_PORT,
  SERIAL_RATE,
  HOSTNAME,
  PORT,
  PATH,
  INTERVAL,
  FPS_SOURCE,
  MODE
};


constexpr int SENSOR_CONFIG_LEN = 25;

const static std::string SENSOR_CONFIG_DIC[SENSOR_CONFIG_LEN]=
{
  "cpu_temp",
  "cpu_tdp",
  "cpu_avg_usage",
  "mem_usage_number",
  "mem_usage_rate",
  "gpu_temp",
  "gpu_tdp",
  "gpu_core_freq",
  "gpu_core_volt",
  "gpu_core_usage_number",
  "gpu_limit_power",
  "gpu_limit_heat",
  "gpu_mem_freq",
  "gpu_mem_usage_number",
  "gpu_mem_usage_rate",
  "rtss_fps",
  "present_mon_fps"
};

enum SENSOR_CONFIG_INDEX {
  CPU_TEMP,
  CPU_TDP,
  CPU_AVG_USAGE,
  MEM_USAGE_NUMBER,
  MEM_USAGE_RATE,
  GPU_TEMP,
  GPU_TDP,
  GPU_CORE_FREQ,
  GPU_CORE_VOLT,
  GPU_CORE_USAGE_NUMBER,
  GPU_LIMIT_POWER,
  GPU_LIMIT_HEAT,
  GPU_MEM_FREQ,
  GPU_MEM_USAGE_NUMBER,
  GPU_MEM_USAGE_RATE,
  RTSS_FPS,
  PRESENT_MON_FPS
};
enum FPS_SOURCE {
  RTSS = 1,
  PRESENT_MON = 2
};
enum MODE {
  NETWORK = 1,
  SERIAL = 2
};

struct SEND_CONFIG {
  int mode;
  httplib::Client network_client;
};
int read_config(ESP32_CONFIG& config,SENSOR_ADDR_VECTOR& sensor_addr_list);
void show_config(ESP32_CONFIG& config);
void inti_send(SEND_CONFIG& send_config);
#endif // CONFIG_H
