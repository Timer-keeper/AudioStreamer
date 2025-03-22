cmake_minimum_required(VERSION 3.10.2)
project(opus_wrapper)

# 指向仓库根目录下的opus源码
set(OPUS_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opus-1.3.1)
add_subdirectory(${OPUS_SOURCE_DIR} opus)

add_library(opus_wrapper SHARED opus_wrapper.cpp)
find_library(log-lib log)
target_link_libraries(opus_wrapper opus ${log-lib})