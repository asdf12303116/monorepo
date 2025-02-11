#include <global.h>
#include <WebServer.h>


struct sensor_data sensor_data={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,false,false};
struct sensor_data * sensor_data_point = &sensor_data;
LinkedList<int> cpu_freq_data;

LinkedList<int>  cpu_usage_data;

int last_sensor_update = NETWORK_UPDATE;
bool sensor_data_updated = false;
bool sensor_data_update_cpu = false;
bool cpu_number_update = false;
int logical_cpu_count = 0;
int physical_cpu_count = 0;

int DATA_TIMEOUT_SEC = 120;
bool data_received = false;
int last_data_received_time = 0;
bool server_is_init = false;

bool tft_bl_on = true;
ESP_PanelBacklight *backlight;


long startTime;
long check_net_ms;
const char* ssid = "HZC902";
const char* password = "1596312345";
#define NTP_SERVER  "cn.pool.ntp.org"
const long utcOffsetInSeconds = 28800; // Beijing: UTC +8  -- 获取东八区时间(默认以英国格林威治天文台所在地的本初子午线为基准线的)
ESP32Time rtc;
tm timeinfo;
int port = 9780;

JsonDocument serial_json_data;
bool serial_json_data_updated = false;
AsyncWebServer server(port);