#ifndef SENSOR_H
#define SENSOR_H
#include <data.h>
#include <hwisenssm2.h>
#include "json/json.h"
#include <format>



struct SENSOR_ADDR {
    std::string name;
    DWORD dwSensorID;
    DWORD dwReadingID;
    DWORD dwSensorInst;
};

typedef std::vector<SENSOR_ADDR> SENSOR_ADDR_VECTOR;

struct SENSOR_DTO_ADDR_VECTOR {
    SENSOR_ADDR_VECTOR cpu_freq_addr_vector;
    SENSOR_ADDR_VECTOR cpu_usage_addr_vector;
    SENSOR_ADDR_VECTOR other_addr_vector;
};

struct SENSOR_READING_DATA {
    DWORD dwSensorID;
    DWORD dwSensorInst;
    SENSOR_READING_TYPE tReading;
    DWORD dwReadingID;
    PHWiNFO_SENSORS_READING_ELEMENT sensorReadingElement;
};

typedef std::vector<SENSOR_READING_DATA> SENSOR_READING_DATA_VECTOR;

struct SENSOR_DATA_READING {
    std::string name;
    PHWiNFO_SENSORS_READING_ELEMENT sensorReadingElement;
};

typedef std::vector<SENSOR_DATA_READING> SENSOR_DATA_READING_VECTOR;

struct SENSOR_DTO_READING_VECTOR {
    SENSOR_DATA_READING_VECTOR cpu_freq_addr_vector;
    SENSOR_DATA_READING_VECTOR cpu_usage_addr_vector;
    SENSOR_DATA_READING_VECTOR other_addr_vector;
    int size = 0;
    int physical_cpu_count = 0;
    int logical_cpu_count= 0;
};

struct BASIC_SHOW_DATA {
    std::string name;
    std::string value;
};

typedef std::vector<BASIC_SHOW_DATA> SHOW_DATA_VECTOR;

constexpr int GPU_VOLTAGE_CONVERT = 1000;
constexpr double GPU_VOLTAGE_MAX = 3000;
constexpr double GPU_VOLTAGE_MIN = 1;

int get_sensor_reading_data(const SENSOR_ADDR_VECTOR &, SENSOR_DTO_READING_VECTOR & sensor_data_reading_vector,SENSOR_DTO_ADDR_VECTOR& sensor_dto_addr_vector,bool & data_error);
std::string reading_sensor(const SENSOR_DTO_READING_VECTOR & sensor_data_reading_vector,int fps_source,Json::FastWriter & fwriter,bool & data_error);
#endif // SENSOR_H
