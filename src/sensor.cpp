#include <config.h>
#include <sensor.h>
#include <core.h>
#include <logger.h>


int GetSharedData(SENSOR_READING_DATA_VECTOR & sensor_reading_data_vector) {
    HANDLE hHWiNFOMemory = OpenFileMapping(FILE_MAP_READ, FALSE, HWiNFO_SENSORS_MAP_FILE_NAME2);
    if (hHWiNFOMemory) {
        PHWiNFO_SENSORS_SHARED_MEM2 pHWiNFOMemory =
                (PHWiNFO_SENSORS_SHARED_MEM2) MapViewOfFile(hHWiNFOMemory, FILE_MAP_READ, 0, 0, 0);
        std::vector<PHWiNFO_SENSORS_SENSOR_ELEMENT> sensor_elements;
        for (DWORD dwSensor = 0; dwSensor < pHWiNFOMemory->dwNumSensorElements; dwSensor++) {
            PHWiNFO_SENSORS_SENSOR_ELEMENT sensor = (PHWiNFO_SENSORS_SENSOR_ELEMENT) ((BYTE *) pHWiNFOMemory +
                pHWiNFOMemory->dwOffsetOfSensorSection +
                (pHWiNFOMemory->dwSizeOfSensorElement * dwSensor));
            sensor_elements.push_back(sensor);
            // TODO: process sensor
        }

        for (DWORD dwReading = 0; dwReading < pHWiNFOMemory->dwNumReadingElements; dwReading++) {
            PHWiNFO_SENSORS_READING_ELEMENT reading = (PHWiNFO_SENSORS_READING_ELEMENT) ((BYTE *) pHWiNFOMemory +
                pHWiNFOMemory->dwOffsetOfReadingSection +
                (pHWiNFOMemory->dwSizeOfReadingElement * dwReading));
            auto *reading_group_data = new SENSOR_READING_DATA;
            reading_group_data->tReading = reading->tReading;
            reading_group_data->dwReadingID = reading->dwReadingID;
            reading_group_data->dwSensorID = sensor_elements.at(reading->dwSensorIndex)->dwSensorID;
            reading_group_data->dwSensorInst = sensor_elements.at(reading->dwSensorIndex)->dwSensorInst;
            reading_group_data->sensorReadingElement = reading;
            sensor_reading_data_vector.push_back(*reading_group_data);
        }
        return 1;
    } else {
        return -1;
    }
    
}

int get_sensor_reading_data(const SENSOR_ADDR_VECTOR & sensor_addr_list, SENSOR_DTO_READING_VECTOR & sensor_dto_reading_vector,SENSOR_DTO_ADDR_VECTOR& sensor_dto_addr_vector,bool & data_error) {
    constexpr DWORD CPU_BASE_SENSOR_ID = 0xf0000300;
    constexpr DWORD CPU_BASE_FREQ_READING_ID = 0x6000000;
    constexpr DWORD CPU_BASE_USAGE_READING_ID = 0x7000000;

    // 获取CPU信息
    int* core_info = cores();
    const int physical_cpu_count = core_info[0];
    const int logical_cpu_count = core_info[1];
    spdlog::info("Loading sensor addr data...");
    // spdlog::info("current cpu cores: {}({})", physical_cpu_count, logical_cpu_count);
    sensor_dto_reading_vector.logical_cpu_count = logical_cpu_count;
    sensor_dto_reading_vector.physical_cpu_count = physical_cpu_count;

    // 清空原始数据
    sensor_dto_addr_vector.other_addr_vector.clear();
    // 获取CPU频率和使用率的sensor地址
    sensor_dto_addr_vector.cpu_freq_addr_vector.clear();
    sensor_dto_addr_vector.cpu_usage_addr_vector.clear();

    // 获取cpu freq/usage Sensor地址
    for (int i = 0; i < physical_cpu_count; i++) {
        std::string name = "cpu" + std::to_string(i)+ "_freq";
        sensor_dto_addr_vector.cpu_freq_addr_vector.push_back({ name, CPU_BASE_SENSOR_ID, CPU_BASE_FREQ_READING_ID + i,0x0});
    }
    for (int i = 0; i < logical_cpu_count; i++) {
        std::string name = "cpu" + std::to_string(i)+ "_usage_rate";
        sensor_dto_addr_vector.cpu_usage_addr_vector.push_back({name, CPU_BASE_SENSOR_ID, CPU_BASE_USAGE_READING_ID + i,0x0});
    }
    // 获取其他Sensor地址
    for (const auto& addr : sensor_addr_list) {
        sensor_dto_addr_vector.other_addr_vector.push_back(addr); 
    } 

    // 获取HWiNFO64共享内存数据
    SENSOR_READING_DATA_VECTOR hwinfo_raw_data_vector;
    int hwinfo_data_result = GetSharedData(hwinfo_raw_data_vector);
    if (hwinfo_data_result < 0) {
        spdlog::error("Get HWiNFO64 Shared Memory Data Error");
        return -1;
    }

    // 获取Sensor数据

    // 清空旧数据
    sensor_dto_reading_vector.cpu_freq_addr_vector.clear();
    sensor_dto_reading_vector.cpu_usage_addr_vector.clear();
    sensor_dto_reading_vector.other_addr_vector.clear();
    sensor_dto_reading_vector.size = 0;
    
    for (auto raw_data : hwinfo_raw_data_vector) {
        for (const auto& cpu_freq_addr : sensor_dto_addr_vector.cpu_freq_addr_vector) {
            if (raw_data.dwReadingID == cpu_freq_addr.dwReadingID
                && raw_data.dwSensorID == cpu_freq_addr.dwSensorID
                && raw_data.dwSensorInst == cpu_freq_addr.dwSensorInst) {
                sensor_dto_reading_vector.size++;
                sensor_dto_reading_vector.cpu_freq_addr_vector.push_back({ cpu_freq_addr.name, raw_data.sensorReadingElement});
            }
        }
        for (const auto& cpu_usage_addr : sensor_dto_addr_vector.cpu_usage_addr_vector) {
            if (raw_data.dwReadingID == cpu_usage_addr.dwReadingID
                && raw_data.dwSensorID == cpu_usage_addr.dwSensorID
                && raw_data.dwSensorInst == cpu_usage_addr.dwSensorInst) {
                sensor_dto_reading_vector.size++;
                sensor_dto_reading_vector.cpu_usage_addr_vector.push_back({ cpu_usage_addr.name, raw_data.sensorReadingElement});
            }
        }
        for (const auto& other_addr : sensor_dto_addr_vector.other_addr_vector) {
            if (raw_data.dwReadingID == other_addr.dwReadingID
                && raw_data.dwSensorID == other_addr.dwSensorID
                && raw_data.dwSensorInst == other_addr.dwSensorInst) {
                sensor_dto_reading_vector.size++;
                sensor_dto_reading_vector.other_addr_vector.push_back({ other_addr.name, raw_data.sensorReadingElement});
            }
        }
    } 
    data_error = false;
    return 0;
}

std::string reading_sensor(const SENSOR_DTO_READING_VECTOR & sensor_dto_reading_vector, const int fps_source,Json::FastWriter & fwriter,bool & data_error) {
    Json::Value json(Json::objectValue);
    Json::Value show_data(Json::objectValue);
    Json::Value cpu_freq_show_data(Json::objectValue);
    Json::Value cpu_usage_show_data(Json::objectValue);
    int physical_cpu_count = sensor_dto_reading_vector.physical_cpu_count;
    int logical_cpu_count = sensor_dto_reading_vector.logical_cpu_count;
    
    for (const auto& data : sensor_dto_reading_vector.other_addr_vector) {
        if (data.name == "gpu_core_volt") {
            show_data[data.name] = std::to_string(data.sensorReadingElement->Value * GPU_VOLTAGE_CONVERT); 
        } else {
            show_data[data.name] = std::to_string(data.sensorReadingElement->Value);
        }
    }

    
    double total_cpu_freq = 0;
    for (const auto& data : sensor_dto_reading_vector.cpu_freq_addr_vector) {
        total_cpu_freq += data.sensorReadingElement->Value;
        cpu_freq_show_data[data.name] = std::to_string(data.sensorReadingElement->Value);
    }

    double cpu_freq = total_cpu_freq/physical_cpu_count;

    show_data["cpu_freq"] = cpu_freq;
    for (const auto& data : sensor_dto_reading_vector.cpu_usage_addr_vector) {
        cpu_usage_show_data[data.name] = std::to_string(data.sensorReadingElement->Value);
    }

     switch (fps_source) {
         case RTSS:
             show_data["fps"] = show_data[SENSOR_CONFIG_DIC[RTSS_FPS]];
             break;{
         }
         case PRESENT_MON:
             show_data["fps"] = show_data[SENSOR_CONFIG_DIC[PRESENT_MON]];
             break;
     }
    
    json["logical_cpu_count"] = logical_cpu_count;
    json["physical_cpu_count"] = physical_cpu_count;
    json["cpu_freq_data"] = cpu_freq_show_data;
    json["cpu_usage_data"] = cpu_usage_show_data;
    json["data"] = show_data;

    // 检查数据
    std::string gpu_core_volt = show_data["gpu_core_volt"].asString();
    if (gpu_core_volt.empty()) {
        std::string error_msg = "gpu_core_volt value error: " + gpu_core_volt;
        spdlog::error(error_msg);
        data_error = true;
    } else{
        double gpu_core_volt_value = std::stod(gpu_core_volt);
        if (gpu_core_volt_value > GPU_VOLTAGE_MAX || gpu_core_volt_value < GPU_VOLTAGE_MIN) {
            std::string error_msg = "gpu_core_volt value error: " + gpu_core_volt;
            spdlog::error(error_msg);
            data_error = true;
        }   
    }

    return fwriter.write(json);
}