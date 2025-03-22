cmake_minimum_required(VERSION 3.12)
project(AudioReceiver)

# 自动编译Opus
include(FetchContent)
FetchContent_Declare(
    opus_src
    URL ${CMAKE_SOURCE_DIR}/opus-1.3.1.tar.gz
)
FetchContent_MakeAvailable(opus_src)

FetchContent_Declare(
       portaudio
       URL https://github.com/PortAudio/portaudio/archive/refs/tags/v19.7.0.tar.gz
   )
   FetchContent_MakeAvailable(opus_src portaudio)

add_executable(receiver src/main.cpp)
target_link_libraries(receiver opus portaudio ws2_32)