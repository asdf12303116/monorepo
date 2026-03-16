//
// Created by a6757 on 2025-07-31.
//

#ifndef ESP32_MONITOR_MQTT_H
#define ESP32_MONITOR_MQTT_H

#include <WiFi.h>
#include <PsychicMqttClient.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>
#include "global.h"
#include "ui.h"
#include "update.h"



void init_mqtt();

#endif //ESP32_MONITOR_MQTT_H
