cmake_minimum_required(VERSION 3.10.2)

# 修正Opus源码路径
add_subdirectory(
  ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opus-1.3.1 
  opus
)

add_library(opus_wrapper SHARED opus_wrapper.cpp)
find_library(log-lib log)
target_link_libraries(opus_wrapper opus ${log-lib})