#include <global.h>
#include <Lcd.h>
#include <ui.h>

void change_cpu_number_bar() {
    // Serial.println("check cpu number bar..");
    // Serial.printf("cpu count: %d(%d)\n",physical_cpu_count,logical_cpu_count);
    if (sensor_data_updated) {
        int cpu_nar_total_number = lv_obj_get_child_cnt(ui_Container6);
        // Serial.printf("cpu number bar is: %d\n",cpu_nar_total_number);

        for (int i = cpu_nar_total_number - 1  ; i > 0; --i) {
            if (i >= logical_cpu_count) {
                lv_obj_add_flag(lv_obj_get_child(ui_Container6, i), LV_OBJ_FLAG_HIDDEN);
            }
        }
    }
}

void update_show_data() {

    lv_label_set_text_fmt(ui_time, "%s", rtc.getTime());

    if (sensor_data_updated) {
        change_cpu_number_bar();
            // Serial.print("cpu number bar update:");
            for (int i = 0; i < logical_cpu_count; i++) {
                // Serial.printf(" %d:%d",i,cpu_usage_data.get(i));
                lv_bar_set_value(lv_obj_get_child(ui_Container6, i), cpu_usage_data.get(i), LV_ANIM_OFF);
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

void update_data(JsonDocument data, enum UPDATE_TYPE type) {
    // Serial.print("[");
    // Serial.print(rtc.getDateTime(true));
    // Serial.print("]");
    // Serial.println("Updating data...");

    lcd_check();
    data_timeout_set();
    
    

    logical_cpu_count = data["logical_cpu_count"];
    physical_cpu_count = data["physical_cpu_count"];

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

    last_sensor_update = type;
    sensor_data_updated = true;

    // Serial.println("Show update sensor data:");
    // print_sensor_data();


    //FPS
    //lv_label_set_text_fmt(ui_real_fps, "%d", random(10, 300));

    //CPU
}

void read_data(String str) {
    DeserializationError error = deserializeJson(serial_json_data, str);
    if (error) {
        serial_json_data_updated = false;
    } else {
        serial_json_data_updated = true;
    }
}