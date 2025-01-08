#ifndef ESP32_UPDATE_H
#define ESP32_UPDATE_H

void change_cpu_number_bar();

void update_show_data();

void check_data_timeout();

void update_data(JsonDocument data, enum UPDATE_TYPE type);

void read_data(String str);
#endif // ESP32_UPDATE_H