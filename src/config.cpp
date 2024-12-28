#include "config.h"
#include <logger.h>
#include "INIReader.h"


int read_config(ESP32_CONFIG& config,SENSOR_ADDR_VECTOR& sensor_addr_list) {
  
  INIReader reader("./config.ini");
  
  if (reader.ParseError() < 0) {
    spdlog::error("Can't load config.ini");
    return -1;
  }

  config.hostname = reader.Get(BASIC_CONFIG, ESP32_CONFIG_DIC[HOSTNAME], "");
  config.interval = reader.GetInteger(BASIC_CONFIG, ESP32_CONFIG_DIC[INTERVAL], 500);
  config.port = reader.GetInteger(BASIC_CONFIG, ESP32_CONFIG_DIC[PORT], 0);
  config.serial_port = reader.Get(BASIC_CONFIG, ESP32_CONFIG_DIC[SERIAL_PORT], "");
  config.serial_rate = reader.GetInteger(BASIC_CONFIG, ESP32_CONFIG_DIC[SERIAL_RATE], 0);
  config.path = reader.Get(BASIC_CONFIG, ESP32_CONFIG_DIC[PATH], "");
  config.fps_source = reader.GetInteger(BASIC_CONFIG, ESP32_CONFIG_DIC[FPS_SOURCE], 0);
  config.mode = reader.GetInteger(BASIC_CONFIG, ESP32_CONFIG_DIC[MODE], 0);

  for (const auto& sensor_config_dic : SENSOR_CONFIG_DIC) {
    if (!sensor_config_dic.empty()) {
      SENSOR_ADDR sensor_addr;
      sensor_addr.name = sensor_config_dic;
      sensor_addr.dwSensorID = reader.GetInteger64(sensor_config_dic, "SensorID", 0);
      sensor_addr.dwReadingID = reader.GetInteger64(sensor_config_dic, "ReadingID", 0);
      sensor_addr.dwSensorInst = reader.GetInteger(sensor_config_dic, "SensorInst", 0);

      if (sensor_addr.dwSensorID == 0 || sensor_addr.dwReadingID == 0) {
        spdlog::warn("Sensor config read error: {}", sensor_config_dic);
      } else {
        sensor_addr_list.push_back(sensor_addr);
      }
    }

  }
  
  return 0;
}

void show_config(ESP32_CONFIG& config) {
  spdlog::info("Show Config:");
  switch (config.mode) {
    case NETWORK:
      spdlog::info("Mode: Network");
      spdlog::info("Hostname: {} Port: {} Path: {}",config.hostname, config.port, config.path);
      break;
    case SERIAL:
      spdlog::info("Mode: Serial");
      spdlog::info("SerialPort: {} SerialRate: {}",config.serial_port, config.serial_rate);
      break;
    default: break;
  }
  spdlog::info("Interval: {} FpsSource: {}",config.interval,  config.fps_source);
}

void inti_send(SEND_CONFIG& send_config) {
  
}