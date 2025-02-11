#include <WebServer.h>
#include <ui.h>
#include <Lcd.h>
#include <update.h>

void print_sensor_data() {
    Serial.print("sensor_data: ");
    Serial.print(sensor_data_point->fps);
    Serial.print(" ");
    Serial.print(sensor_data_point->cpu_freq);
    Serial.print(" ");
    Serial.print(sensor_data_point->cpu_avg_usage);
    Serial.print(" ");
    Serial.print(sensor_data_point->cpu_temp);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_core_freq);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_core_usage_number);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_core_volt);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_mem_freq);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_mem_usage_number);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_mem_usage_rate);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_tdp);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_temp);
    Serial.print(" ");
    Serial.print(sensor_data_point->mem_usage_number);
    Serial.print(" ");
    Serial.print(sensor_data_point->mem_usage_rate);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_limit_power);
    Serial.print(" ");
    Serial.print(sensor_data_point->gpu_limit_heat);

    Serial.print("cpu_usage_data: ");
    if (cpu_usage_data.size() > 0) {
        for (int i = 0; i < cpu_usage_data.size(); i++) {
            Serial.print(cpu_usage_data[i]);
            Serial.print(" ");
        }

    }
    Serial.println("");
}

void notFound(AsyncWebServerRequest *request) {
    request->send(404, "text/plain", "Not found");
}







void data_timeout_set() {
    last_data_received_time = millis();
}

void init_server() {
    if (WiFi.status() == WL_CONNECTED && !server_is_init) {
        Serial.println("Initializing server...");
        configTime(utcOffsetInSeconds, 0, NTP_SERVER);
        server_is_init = true;
        Serial.print("server is at ");
        String ip_port= WiFi.localIP().toString()+":"+String(port);
        Serial.println(ip_port);
        lv_label_set_text(ui_local_ip_addr, ip_port.c_str());
        server.onNotFound(notFound);
        server.on("/", HTTP_GET, [](AsyncWebServerRequest *request) {
            request->send(200, "text/plain", "Hello, world!");
        });
        server.on("/hello", HTTP_GET, [](AsyncWebServerRequest *request) {
            request->send(200, "text/plain", "Hello!");
        });
        AsyncCallbackJsonWebHandler *handler = new AsyncCallbackJsonWebHandler(
                "/update_status", [](AsyncWebServerRequest *request, JsonVariant &json) {
                    JsonDocument data;
                    if (json.is<JsonObject>()) {
                        data = json.as<JsonObject>();
                        update_data(data,NETWORK_UPDATE);
                    }
                    request->send(200, "application/json", "ok");
                });
        server.addHandler(handler);
        server.begin();
    }
}