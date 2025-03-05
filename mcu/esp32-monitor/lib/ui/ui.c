// This file was generated by SquareLine Studio
// SquareLine Studio version: SquareLine Studio 1.5.0
// LVGL version: 8.3.11
// Project name: SquareLine_Project

#include "ui.h"
#include "ui_helpers.h"

///////////////////// VARIABLES ////////////////////


// SCREEN: ui_Screen1
void ui_Screen1_screen_init(void);
lv_obj_t * ui_Screen1;
lv_obj_t * ui_background;
lv_obj_t * ui_row1;
lv_obj_t * ui_Container1;
lv_obj_t * ui_Container3;
lv_obj_t * ui_local_ip;
lv_obj_t * ui_local_ip_addr;
lv_obj_t * ui_Container2;
lv_obj_t * ui_time;
lv_obj_t * ui_Container5;
lv_obj_t * ui_real_fps;
lv_obj_t * ui_fps;
lv_obj_t * ui_cpu_info;
lv_obj_t * ui_Container7;
lv_obj_t * ui_Container9;
lv_obj_t * ui_gpu_name2;
lv_obj_t * ui_total_mem3;
lv_obj_t * ui_used_cpu_num;
lv_obj_t * ui_mem_mb4;
lv_obj_t * ui_core_temp_info;
lv_obj_t * ui_cpu_core_temp;
lv_obj_t * ui_gpu_mem_mb3;
lv_obj_t * ui_Container8;
lv_obj_t * ui_gpu_power2;
lv_obj_t * ui_used_cpu_power;
lv_obj_t * ui_walt2;
lv_obj_t * ui_e_core_freq_group;
lv_obj_t * ui_e_core_freq;
lv_obj_t * ui_e_core_mhz;
lv_obj_t * ui_p_core_freq_group;
lv_obj_t * ui_p_core_freq;
lv_obj_t * ui_p_core_mhz;
lv_obj_t * ui_Container6;
lv_obj_t * ui_cpu_core1;
lv_obj_t * ui_cpu_core2;
lv_obj_t * ui_cpu_core3;
lv_obj_t * ui_cpu_core4;
lv_obj_t * ui_cpu_core5;
lv_obj_t * ui_cpu_core6;
lv_obj_t * ui_cpu_core7;
lv_obj_t * ui_cpu_core8;
lv_obj_t * ui_cpu_core9;
lv_obj_t * ui_cpu_core10;
lv_obj_t * ui_cpu_core11;
lv_obj_t * ui_cpu_core12;
lv_obj_t * ui_cpu_core13;
lv_obj_t * ui_cpu_core14;
lv_obj_t * ui_cpu_core15;
lv_obj_t * ui_cpu_core16;
lv_obj_t * ui_mem_info;
lv_obj_t * ui_mem_text;
lv_obj_t * ui_mem_name;
lv_obj_t * ui_total_mem;
lv_obj_t * ui_used_mem_number;
lv_obj_t * ui_mem_mb;
lv_obj_t * ui_total_mem1;
lv_obj_t * ui_used_mem;
lv_obj_t * ui_mem_mb1;
lv_obj_t * ui_used_mem_bar;
lv_obj_t * ui_gpu_info;
lv_obj_t * ui_gpu_text_info;
lv_obj_t * ui_gpu_name;
lv_obj_t * ui_gpu_temp;
lv_obj_t * ui_gpu_core_temp;
lv_obj_t * ui_gpu_mem_mb;
lv_obj_t * ui_gpu_power;
lv_obj_t * ui_uesd_gpu_power;
lv_obj_t * ui_walt;
lv_obj_t * ui_gpu_freq_info;
lv_obj_t * ui_gpu_freq;
lv_obj_t * ui_Mhz;
lv_obj_t * ui_gpu_core_v;
lv_obj_t * ui_gpu_core_volt;
lv_obj_t * ui_Volt;
lv_obj_t * ui_gpu_info_1;
lv_obj_t * ui_gpu_core_load_info;
lv_obj_t * ui_gpu_core_load;
lv_obj_t * ui_gpu_mem_mb1;
lv_obj_t * ui_gpu_load_bar;
lv_obj_t * ui_gpu_mem_name2;
lv_obj_t * ui_gpu_load_status;
lv_obj_t * ui_used_gpu_mem_number2;
lv_obj_t * ui_gpu_mem_info;
lv_obj_t * ui_gpu_mem_name;
lv_obj_t * ui_gpu_mem_freq_info;
lv_obj_t * ui_gpu_mem_freq;
lv_obj_t * ui_gpu_mem_mhz;
lv_obj_t * ui_gpu_mem_info_show;
lv_obj_t * ui_used_gpu_mem;
lv_obj_t * ui_Mhz2;
lv_obj_t * ui_gpu_mem_number;
lv_obj_t * ui_used_gpu_mem_number;
lv_obj_t * ui_mem_mb3;
lv_obj_t * ui_used_gpu_mem_bar;
// CUSTOM VARIABLES
lv_obj_t * uic_row1;
lv_obj_t * uic_Container1;
lv_obj_t * uic_local_ip;
lv_obj_t * uic_local_ip_addr;
lv_obj_t * uic_time;
lv_obj_t * uic_real_fps;
lv_obj_t * uic_fps;
lv_obj_t * uic_cpu_info;
lv_obj_t * uic_gpu_name;
lv_obj_t * uic_used_cpu_num;
lv_obj_t * uic_core_temp_info;
lv_obj_t * uic_cpu_core_temp;
lv_obj_t * uic_gpu_mem_mb;
lv_obj_t * uic_gpu_power;
lv_obj_t * uic_used_cpu_power;
lv_obj_t * uic_gpu_freq_info;
lv_obj_t * uic_Mhz;
lv_obj_t * uic_gpu_freq_info;
lv_obj_t * uic_Mhz;
lv_obj_t * uic_mem_info;
lv_obj_t * uic_used_mem_number;
lv_obj_t * uic_used_mem;
lv_obj_t * uic_used_mem_bar;
lv_obj_t * uic_gpu_info;
lv_obj_t * uic_gpu_text_info;
lv_obj_t * uic_gpu_name;
lv_obj_t * uic_gpu_temp;
lv_obj_t * uic_gpu_core_temp;
lv_obj_t * uic_gpu_mem_mb;
lv_obj_t * uic_gpu_power;
lv_obj_t * uic_uesd_gpu_power;
lv_obj_t * uic_gpu_freq_info;
lv_obj_t * uic_gpu_freq;
lv_obj_t * uic_Mhz;
lv_obj_t * uic_gpu_core_v;
lv_obj_t * uic_gpu_core_volt;
lv_obj_t * uic_Volt;
lv_obj_t * uic_gpu_info_1;
lv_obj_t * uic_gpu_core_load_info;
lv_obj_t * uic_gpu_core_load;
lv_obj_t * uic_gpu_mem_mb;
lv_obj_t * uic_gpu_load_bar;
lv_obj_t * uic_gpu_mem_name;
lv_obj_t * uic_gpu_load_status;
lv_obj_t * uic_used_gpu_mem_number;
lv_obj_t * uic_gpu_mem_info;
lv_obj_t * uic_gpu_mem_name;
lv_obj_t * uic_gpu_mem_freq_info;
lv_obj_t * uic_gpu_mem_freq;
lv_obj_t * uic_gpu_mem_mhz;
lv_obj_t * uic_gpu_mem_info_show;
lv_obj_t * uic_used_gpu_mem;
lv_obj_t * uic_Mhz;
lv_obj_t * uic_gpu_mem_number;
lv_obj_t * uic_used_gpu_mem_number;

// EVENTS
lv_obj_t * ui____initial_actions0;

// IMAGES AND IMAGE SETS

///////////////////// TEST LVGL SETTINGS ////////////////////
#if LV_COLOR_DEPTH != 16
    #error "LV_COLOR_DEPTH should be 16bit to match SquareLine Studio's settings"
#endif
#if LV_COLOR_16_SWAP !=0
    #error "LV_COLOR_16_SWAP should be 0 to match SquareLine Studio's settings"
#endif

///////////////////// ANIMATIONS ////////////////////

///////////////////// FUNCTIONS ////////////////////

///////////////////// SCREENS ////////////////////

void ui_init(void)
{
    lv_disp_t * dispp = lv_disp_get_default();
    lv_theme_t * theme = lv_theme_basic_init(dispp);
    lv_disp_set_theme(dispp, theme);
    ui_Screen1_screen_init();
    ui____initial_actions0 = lv_obj_create(NULL);
    lv_disp_load_scr(ui_Screen1);
}
