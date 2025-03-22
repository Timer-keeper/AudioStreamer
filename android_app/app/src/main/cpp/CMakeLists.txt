cmake_minimum_required(VERSION 3.10.2)

# 明确定义项目名称
project(opus_wrapper)

# 路径修正
set(OPUS_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opus-1.3.1)
add_subdirectory(${OPUS_SOURCE_DIR} opus)

add_library(opus_wrapper SHARED opus_wrapper.cpp)
find_library(log-lib log)
target_link_libraries(opus_wrapper opus ${log-lib})