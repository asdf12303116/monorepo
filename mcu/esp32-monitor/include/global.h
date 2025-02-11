#ifndef ESP32_GLOBAL_H
#define ESP32_GLOBAL_H
#include <LinkedList.h>
#include <ESP_Panel_Library.h>
#include <ESP32Time.h>
#include "ESPAsyncWebServer.h"
#include <WebServer.h>
#include <ArduinoJson.h>


extern  struct sensor_data sensor_data;
extern  struct sensor_data * sensor_data_point;

extern LinkedList<int> cpu_freq_data;

extern LinkedList<int>  cpu_usage_data;

extern int last_sensor_update;
extern bool sensor_data_updated;
extern bool sensor_data_update_cpu;
extern bool cpu_number_update;
extern int logical_cpu_count;
extern int physical_cpu_count;

extern int DATA_TIMEOUT_SEC;
extern bool data_received;
extern int last_data_received_time;
extern bool server_is_init;
extern AsyncWebServer server;

extern bool tft_bl_on;
extern ESP_PanelBacklight * backlight;

extern long startTime;
extern long check_net_ms;
extern const char* ssid;
extern const char* password;
#define NTP_SERVER  "cn.pool.ntp.org"
extern const long utcOffsetInSeconds; // Beijing: UTC +8  -- 获取东八区时间(默认以英国格林威治天文台所在地的本初子午线为基准线的)
extern ESP32Time rtc;
extern tm timeinfo;
extern int port;

extern JsonDocument serial_json_data;
extern bool serial_json_data_updated;
#endif // ESP32_GLOBAL_H