#include <network.h>
#include <format>
#include <logger.h>



httplib::Client init_client(ESP32_CONFIG &config) {
    std::string Host = std::format("http://{}:{}",config.hostname,config.port);
    httplib::Client client = httplib::Client(Host);
    return client;
}
void send_data(ESP32_CONFIG &config,httplib::Client& client,const std::string &jsonStr) {
    auto res = client.Post(config.path,jsonStr,"application/json");
    if (!res) {
        std::string error = to_string(res.error());
        std::string error_msg = std::format("Post data error: {}",error);
        spdlog::error(error_msg);
    } 
 
  
}