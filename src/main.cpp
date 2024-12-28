
#include <data.h>
#include <logger.h>
#include <config.h>

#include <sensor.h>
#include <network.h>
#include <csignal>


bool exe_stop = false; 
std::thread::id sigint_id;
bool data_err = false;
 
// Ctrl+C捕获后执行的代码
void sigint_handler(int sig) {
    sigint_id = std::this_thread::get_id();
    exe_stop = true; // 改写标志位
}

void register_signal_handler() {
    signal(SIGINT, sigint_handler);
    signal(SIGTERM, sigint_handler);
    signal(SIGBREAK, sigint_handler);
    signal(SIGABRT, sigint_handler);
}

void pre_exit() {
    // 释放资源
    spdlog::info("exit...");
    spdlog::shutdown();
}
int main() {
    // register_signal_handler();
    atexit(pre_exit);
    init_log();

    // 读取配置
    ESP32_CONFIG config;
    SENSOR_ADDR_VECTOR sensor_addr_list;

    int read_result = read_config(config, sensor_addr_list);

    if (read_result < 0) {
        return -1;
    }
    show_config(config);

    
    // 获取hwinfo64共享内存信息
    SENSOR_DTO_READING_VECTOR sensor_dto_reading_vector;
    SENSOR_DTO_ADDR_VECTOR sensor_dto_addr_vector;
    std::chrono::steady_clock::time_point start = std::chrono::steady_clock::now();
    spdlog::info("Waiting Sensor Data...");
    do {
        if ((std::chrono::steady_clock::now()- start) > std::chrono::minutes(1)) {
            spdlog::error("Sensor Data Getting Time out");
            return -1;
        }
        if (sensor_dto_reading_vector.size == 0) {
            get_sensor_reading_data(sensor_addr_list, sensor_dto_reading_vector,sensor_dto_addr_vector,data_err);
            std::this_thread::sleep_for(std::chrono::seconds(2));
        }  else {
            break;
        }
    } while (true);

    // 初始化json
    Json::FastWriter fwriter;
    std::string test = reading_sensor(sensor_dto_reading_vector,config.fps_source,fwriter,data_err);
    // 进入主循环

    // 初始化network
    httplib::Client client = init_client(config); 


    
    do {
        if (exe_stop) {
            break;
        }
        // 读取传感器数据，校验数据，调整数据至JSON
        if (data_err) {
            get_sensor_reading_data(sensor_addr_list, sensor_dto_reading_vector,sensor_dto_addr_vector,data_err);
        }
        // spdlog::debug("reading sensor data...");
        std::string json_str = reading_sensor(sensor_dto_reading_vector,config.fps_source,fwriter,data_err);
       

        // 输出数据 (网络/串口)

        send_data(config,client,json_str);
        // 等待下一次循环
        
        std::this_thread::sleep_for(std::chrono::milliseconds(config.interval));
    } while (true);
    



    return 0;
}

