#include <global.h>
#include <Lcd.h>

void initLcd_lvgl(){
    Serial.println("Create LCD device");
    ESP_Panel * panel = new ESP_Panel();
    panel->init();
    panel->begin();
    backlight = panel->getBacklight();
    tft_bl_on = true;
    lvgl_port_init(panel->getLcd(), panel->getTouch());
}

void lcd_check() {
    if (!tft_bl_on) {
        Serial.println("turn on lcd light");
        tft_bl_on = true;
        backlight->on();
    }
}

void lcd_turn_off() {
    if (tft_bl_on) {
        Serial.println("turn off lcd light");
        tft_bl_on = false;
        backlight->off();
    }

}