cmake_minimum_required(VERSION 3.10.2)

# 添加Opus源码编译
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../../../../opus-1.3.1 opus)

add_library(opus_wrapper SHARED opus_wrapper.cpp)
target_link_libraries(opus_wrapper opus ${log-lib})