//
// Created by a6757 on 2024/12/26.
//

#ifndef ESP32_NETWORK_H
#define ESP32_NETWORK_H
#include <config.h>
#include "httplib.h"

httplib::Client init_client(ESP32_CONFIG &config);
void send_data(ESP32_CONFIG &config,httplib::Client& client,const std::string &jsonStr);




#endif //ESP32_NETWORK_H
