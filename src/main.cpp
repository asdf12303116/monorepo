#include <Arduino.h>

#include <lvgl.h>
#include <ui.h>
#include <WiFi_Config.h>
#include <WebServer.h>
#include <Ticker.h>
#include <Lcd.h>
#include <global.h>
#include <Config.h>
#include <update.h>

Ticker ticker_lcd;
Ticker ticker_wifi;
long last_ms;





void setupWiFi(){
    startTime = millis();
    auto *config = new Config{"", "", ""};
    getConfig(config);
    print_config(config);
    if (config->ssid == "" & config->password =="") {
        Serial.println("WiFi config not found");
        config->ssid = ssid;
        config->password = password;
        saveConfig(config);
    }
    WiFi.mode(WIFI_MODE_STA);
    WiFi.begin(config->ssid, password);
    delay(1000);
    free(config);
    configTime(utcOffsetInSeconds, 0, NTP_SERVER);
}

void checkWiFi(){
    if (WiFi.status() != WL_CONNECTED) {
        Serial.print("WiFi not connect... try reconnect to ");
        WiFi.disconnect();
        auto *config = new Config{"", "", ""};
        getConfig(config);
        Serial.println(config->ssid);
        WiFi.begin(config->ssid, config->password);
    }
    if (WiFi.status() == WL_CONNECTED) {
        init_server();
    }
}

void setup() {

    Serial.begin(115200);
    initLcd_lvgl();
    last_ms = millis();
    
    ui_init();
    data_timeout_set();
    ticker_wifi.attach(60, checkWiFi);
    ticker_lcd.attach(DATA_TIMEOUT_SEC / 2, check_data_timeout);

    setupWiFi();
    init_server();
    
}

void loop() {
    if (Serial.available() > 0) {
        String msg = Serial.readStringUntil('\n');
        read_data(msg);
        if (serial_json_data_updated) {
            update_data(serial_json_data, SERIAL_UPDATE);
        }
    }
    if (tft_bl_on) {
        lv_timer_handler(); /* let the GUI do its work */
        if (millis() - last_ms >= 500) {
            last_ms = millis();
            update_show_data();
        }
    }
    delay(5);
}