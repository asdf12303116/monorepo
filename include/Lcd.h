
#ifndef ESP32_LCD_TEST_LCD_H
#define ESP32_LCD_TEST_LCD_H
#include <ESP_Panel_Library.h>
bool tft_bl_on = true;
ESP_PanelBacklight *backlight;

void initLcd_lvgl(){
    Serial.println("Create LCD device");
    ESP_Panel * panel = new ESP_Panel();
    panel->init();
    panel->begin();
    backlight = panel->getBacklight();
    tft_bl_on = true;
    lvgl_port_init(panel->getLcd(), panel->getTouch());
}

inline void lcd_check() {
    if (!tft_bl_on) {
        Serial.println("turn on lcd light");
        tft_bl_on = true;
        backlight->on();
    }
}

inline void lcd_turn_off() {
    if (tft_bl_on) {
        Serial.println("turn off lcd light");
        tft_bl_on = false;
        backlight->off();
    }

}

#endif //ESP32_LCD_TEST_LCD_H
