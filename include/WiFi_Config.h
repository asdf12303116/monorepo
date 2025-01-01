#ifndef ESP32_WIFI_CONFIG_H
#define ESP32_WIFI_CONFIG_H
#include <WiFi.h>
#include <ESP32Time.h>
#include <time.h>
#include <Config.h>

static unsigned long startTime;
static unsigned long check_net_ms;
const char* ssid = "HZC902";
const char* password = "1596312345";
#define NTP_SERVER  "cn.pool.ntp.org"
const long utcOffsetInSeconds = 28800; // Beijing: UTC +8  -- 获取东八区时间(默认以英国格林威治天文台所在地的本初子午线为基准线的)
ESP32Time rtc;
tm timeinfo;
constexpr int port = 9780;

void checkWiFi();

void setupWiFi();


#endif // ESP32_WIFI_CONFIG_H
