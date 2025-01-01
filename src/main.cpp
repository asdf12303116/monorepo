#include <Arduino.h>

#include <lvgl.h>
#include <lvgl_port_v8.h>
#include <ui.h>
#include <WiFi_Config.h>
#include <ESP32Time.h>
#include <WebServer.h>
#include <Ticker.h>
#include <Lcd.h>
#include <ui.h>

Ticker ticker_lcd;
Ticker ticker_wifi;
long last_ms;


void change_cpu_number_bar() {
    // Serial.println("check cpu number bar..");
    // Serial.printf("cpu count: %d(%d)\n",physical_cpu_count,logical_cpu_count);
    if (!cpu_number_update && sensor_data_updated) {
        Serial.println("Changing cpu number bar..");
        int cpu_nar_total_number = lv_obj_get_child_cnt(ui_Container6);
        // Serial.printf("cpu number bar is: %d\n",cpu_nar_total_number);

        for (int i = cpu_nar_total_number - 1  ; i > 0; --i) {
            if (i >= logical_cpu_count) {
                lv_obj_add_flag(lv_obj_get_child(ui_Container6, i), LV_OBJ_FLAG_HIDDEN);
            }
        }
        cpu_number_update = true;
    }
}

void update_data() {

    lv_label_set_text_fmt(ui_time, "%s", rtc.getTime());

    if (sensor_data_updated) {
        change_cpu_number_bar();
        if (cpu_number_update) {
            // Serial.print("cpu number bar update:");
            for (int i = 0; i < logical_cpu_count; i++) {
                // Serial.printf(" %d:%d",i,cpu_usage_data.get(i));
                lv_bar_set_value(lv_obj_get_child(ui_Container6, i), cpu_usage_data.get(i), LV_ANIM_OFF);
            }
        }

        if (sensor_data_point->fps != 0) {
            lv_label_set_text_fmt(ui_real_fps, "%d", sensor_data_point->fps);
        } else {
            lv_label_set_text(ui_real_fps,"-");
        }


        lv_label_set_text_fmt(ui_cpu_core_temp, "%d", sensor_data_point->cpu_temp);
        lv_label_set_text_fmt(ui_used_cpu_power, "%d", sensor_data_point->cpu_tdp);
        lv_label_set_text_fmt(ui_used_cpu_num, "%d", sensor_data_point->cpu_avg_usage);
        lv_label_set_text_fmt(ui_cpu_freq, "%d", sensor_data_point->cpu_freq);

        lv_label_set_text_fmt(ui_used_mem_number, "%d", sensor_data_point->mem_usage_number);
        lv_label_set_text_fmt(ui_used_mem, "%d", sensor_data_point->mem_usage_rate);
        lv_bar_set_value(ui_used_mem_bar, sensor_data_point->mem_usage_rate, LV_ANIM_OFF);


        lv_label_set_text_fmt(ui_gpu_core_temp, "%d", sensor_data_point->gpu_temp);
        lv_label_set_text_fmt(ui_uesd_gpu_power, "%d", sensor_data_point->gpu_tdp);
        lv_label_set_text_fmt(ui_gpu_freq, "%d", sensor_data_point->gpu_core_freq);
        lv_label_set_text_fmt(ui_gpu_core_volt, "%d", sensor_data_point->gpu_core_volt);
        lv_label_set_text_fmt(ui_gpu_core_load, "%d", sensor_data_point->gpu_core_usage_number);
        lv_label_set_text_fmt(ui_gpu_mem_freq, "%d", sensor_data_point->gpu_mem_freq);
        lv_label_set_text_fmt(ui_used_gpu_mem, "%d", sensor_data_point->gpu_mem_usage_number);
        lv_label_set_text_fmt(ui_used_gpu_mem_number, "%d", sensor_data_point->gpu_mem_usage_rate);

        lv_bar_set_value(ui_gpu_load_bar, sensor_data_point->gpu_core_usage_number, LV_ANIM_OFF);
        lv_bar_set_value(ui_used_gpu_mem_bar, sensor_data_point->gpu_mem_usage_rate, LV_ANIM_OFF);

        
        if (sensor_data_point->gpu_limit_power) {
            lv_label_set_text_fmt(ui_gpu_load_status, "%s", "Pwr");
        } else if (sensor_data_point->gpu_limit_heat) {
            lv_label_set_text_fmt(ui_gpu_load_status, "%s", "Heat");
        } else {
            lv_label_set_text_fmt(ui_gpu_load_status, "%s", "no Load");
        }
        
        


        sensor_data_updated = false;
    }
}

void check_data_timeout() {
    if (millis() - last_data_received_time > DATA_TIMEOUT_SEC * 1000) {
        if (tft_bl_on) {
            Serial.println("Data timeout, turn off display");
            lcd_turn_off();
        }
    }
}

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
    if (tft_bl_on) {
        lv_timer_handler(); /* let the GUI do its work */
        if (millis() - last_ms >= 500) {
            last_ms = millis();
            update_data();
        }
    }
    delay(5);
}