cmake_minimum_required(VERSION 3.30)
project(esp32_host_newapp)

set(CMAKE_CXX_STANDARD 20)
include_directories(include)
add_executable(esp32_host_newapp src/main.cpp)

file(GLOB_RECURSE SOURCE_FILES "src/*.cpp")
target_sources(esp32_host_newapp PRIVATE ${SOURCE_FILES})


find_package(jsoncpp CONFIG REQUIRED)
find_package(httplib CONFIG REQUIRED)
find_package(unofficial-inih CONFIG REQUIRED)
find_package(spdlog CONFIG REQUIRED)


target_link_libraries(esp32_host_newapp  PRIVATE
        JsonCpp::JsonCpp
        httplib::httplib
        unofficial::inih::inireader
        spdlog::spdlog)

configure_file(config/config.ini ${CMAKE_CURRENT_BINARY_DIR}/config.ini COPYONLY)
