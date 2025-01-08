#ifndef ESP32_WEBSERVER_H
#define ESP32_WEBSERVER_H
#include <AsyncTCP.h>
#include <WiFi.h>
#include <WiFi_Config.h>
#include <ESPAsyncWebServer.h>
#if __has_include("ArduinoJson.h")
#include <ArduinoJson.h>
#include <AsyncJson.h>
#include <AsyncMessagePack.h>
#endif

struct sensor_data {
  int fps;
  int cpu_freq;
  int cpu_avg_usage;
  int cpu_tdp;
  int cpu_temp;
  int gpu_core_freq;
  int gpu_core_usage_number;
  int gpu_core_volt;
  int gpu_mem_freq;
  int gpu_mem_usage_number;
  int gpu_mem_usage_rate;
  int gpu_tdp;
  int gpu_temp;
  int mem_usage_number;
  int mem_usage_rate;
  bool gpu_limit_power;
  bool gpu_limit_heat;
};

enum UPDATE_TYPE {
  NETWORK_UPDATE,
  SERIAL_UPDATE
};



void print_sensor_data();

void notFound(AsyncWebServerRequest *request);







void data_timeout_set();

void init_server();

#endif // ESP32_WEBSERVER_H
