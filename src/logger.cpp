#include "spdlog/spdlog.h"
#include "spdlog/async.h"
#include "spdlog/sinks/stdout_color_sinks.h"
#include "spdlog/sinks/rotating_file_sink.h"

void init_log(){
    auto max_size = 1048576 * 5;
    auto max_files = 1;
    auto file_sink  = std::make_shared<spdlog::sinks::rotating_file_sink_mt>("esp32_host.log", max_size, max_files);
    auto console_sink = std::make_shared<spdlog::sinks::stdout_color_sink_mt>();
    
    file_sink->set_level(spdlog::level::info);
    console_sink->set_level(spdlog::level::debug);
    

    spdlog::init_thread_pool(8192, 1);

    auto async_logger = std::make_shared<spdlog::async_logger>(
       "logger", 
       spdlog::sinks_init_list({console_sink, file_sink}), 
       spdlog::thread_pool(), 
       spdlog::async_overflow_policy::block);

    
    async_logger->set_level(spdlog::level::debug);
   
    spdlog::flush_every(std::chrono::seconds(3));
    
    spdlog::set_default_logger(async_logger);
}