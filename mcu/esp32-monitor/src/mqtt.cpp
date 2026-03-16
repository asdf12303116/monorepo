//
// Created by a6757 on 2025-07-31.
//
#include "mqtt.h"

// MQTT Broker settings
const char *mqtt_broker = "mqtt://101.133.231.235:1883";
const char *mqtt_username = "esp_client";
const char *mqtt_password = "esp_client";
const char *mqtt_topic = "431aec54-77c9-4822-a975-32ba203bec6d_monitor/data";

JsonDocument document;




PsychicMqttClient mqttClient;
void onTopic(const char *topic, const char *payload, int retain, int qos, bool dup){
    DeserializationError error =  deserializeJson(document, payload);
    if (error.code() != DeserializationError::Ok) {
        Serial.print("deserializeJson() failed: ");
        Serial.println(error.c_str());
    } else{
        update_data(document,NETWORK_UPDATE);
    }
    
}



void init_mqtt() {
    if (WiFi.status() == WL_CONNECTED && !mqttClient.connected()) {
        Serial.println("Initializing MQTT server...");
        configTime(utcOffsetInSeconds, 0, NTP_SERVER);
//        server_is_init = true;
        Serial.print("server is at ");
        String ip_port= WiFi.localIP().toString()+":"+String(port);
        Serial.println(ip_port);
        lv_label_set_text(ui_local_ip_addr, ip_port.c_str());

        mqttClient.setServer(mqtt_broker);
        mqttClient.setCredentials(mqtt_username,mqtt_password);
        mqttClient.onTopic(mqtt_topic,0, onTopic);
        mqttClient.connect();
    }
}
