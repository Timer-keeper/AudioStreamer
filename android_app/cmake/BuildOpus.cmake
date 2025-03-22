# 自动解压并编译Opus
ExternalProject_Add(
    opus_src
    URL ${CMAKE_SOURCE_DIR}/../../opus-1.3.1.tar.gz
    CONFIGURE_COMMAND <SOURCE_DIR>/configure --host=${ANDROID_NDK_ABI_NAME}
    BUILD_COMMAND make
    INSTALL_COMMAND ""
)