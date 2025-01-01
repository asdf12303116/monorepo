#ifndef ESP32_CONFIG_H
#define ESP32_CONFIG_H
#include <Preferences.h>

struct Config {
    String ssid;
    String password;
    String city;
};

inline void getConfig(Config *config) {
    Preferences prefs;
    prefs.begin("config");
    config->ssid = prefs.getString("ssid", "");
    config->password = prefs.getString("password", "");
    config->city = prefs.getString("city", "");
    prefs.end(); // 关闭当前命名空间
}


inline void saveConfig(const Config *config) {
    Preferences prefs;
    prefs.begin("config");
    prefs.putString("ssid", config->ssid);
    prefs.putString("password", config->password);
    prefs.putString("city", config->city);
    prefs.end(); // 关闭当前命名空间
}

inline void print_config(const Config *config) {
    Serial.print("ssid: ");
    Serial.println(config->ssid);
    Serial.print("password: ");
    Serial.println(config->password);
    Serial.print("city: ");
    Serial.println(config->city);
}

inline void test_new_config(char serial) {
    auto *config = new Config{String("ssid-") +serial, String("password-")+serial, String("city-")+serial};
    saveConfig(config);
    free(config);
}
#endif // ESP32_CONFIG_H
