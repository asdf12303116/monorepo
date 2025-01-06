#include <WebServer.h>
#include <ui.h>
#include <Lcd.h>

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



void update_data(JsonDocument data) {
    // Serial.print("[");
    // Serial.print(rtc.getDateTime(true));
    // Serial.print("]");
    // Serial.println("Updating data...");

    if (!sensor_data_update_cpu) {
        logical_cpu_count = data["logical_cpu_count"];
        physical_cpu_count = data["physical_cpu_count"];
    }
    //physical_cpu freq

    //logical_cpu usage

    for (int i = 0; i < logical_cpu_count; i++) {
        if (cpu_usage_data.size() == logical_cpu_count) {
            cpu_usage_data[i] = data["cpu_usage_data"]["cpu"+String(i)+"_usage_rate"];
        } else {
            cpu_usage_data.add(data["cpu_usage_data"]["cpu"+String(i)+"_usage_rate"]);
        }

    }


    // basic data
    sensor_data_point->fps =  data["data"]["fps"];
    sensor_data_point->cpu_avg_usage =  data["cpu_avg_usage"];
    sensor_data_point->cpu_freq = data["data"]["cpu_freq"];
    sensor_data_point->cpu_avg_usage = data["data"]["cpu_avg_usage"];
    sensor_data_point->cpu_tdp = data["data"]["cpu_tdp"];
    sensor_data_point->cpu_temp = data["data"]["cpu_temp"];
    sensor_data_point->gpu_core_freq = data["data"]["gpu_core_freq"];
    sensor_data_point->gpu_core_usage_number = data["data"]["gpu_core_usage_number"];
    sensor_data_point->gpu_core_volt = data["data"]["gpu_core_volt"];
    sensor_data_point->gpu_mem_freq = data["data"]["gpu_mem_freq"];
    sensor_data_point->gpu_mem_usage_number = data["data"]["gpu_mem_usage_number"];
    sensor_data_point->gpu_mem_usage_rate = data["data"]["gpu_mem_usage_rate"];
    sensor_data_point->gpu_tdp = data["data"]["gpu_tdp"];
    sensor_data_point->gpu_temp = data["data"]["gpu_temp"];
    sensor_data_point->mem_usage_number = data["data"]["mem_usage_number"];
    sensor_data_point->mem_usage_rate = data["data"]["mem_usage_rate"];
    int gpu_limit_heat = data["data"]["gpu_limit_heat"];
    int gpu_limit_power= data["data"]["gpu_limit_power"];
    sensor_data_point->gpu_limit_heat = gpu_limit_heat == 1;
    sensor_data_point->gpu_limit_power = gpu_limit_power == 1;

    last_sensor_update = NETWORK_UPDATE;
    sensor_data_updated = true;

    // Serial.println("Show update sensor data:");
    // print_sensor_data();


    //FPS
    //lv_label_set_text_fmt(ui_real_fps, "%d", random(10, 300));

    //CPU
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
                        lcd_check();
                        data_timeout_set();
                        data = json.as<JsonObject>();
                        update_data(data);
                    }
                    request->send(200, "application/json", "ok");
                });
        server.addHandler(handler);
        server.begin();
    }
}