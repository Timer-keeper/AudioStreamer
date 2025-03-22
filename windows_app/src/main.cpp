#include <opus/opus.h>
#include <portaudio.h>
#include <thread>
#include <queue>
#include <mutex>

OpusDecoder* decoder;
std::queue<short> audioBuffer;
std::mutex bufferMutex;

static int audioCallback(const void*, void* outBuffer, 
    unsigned long frames, const PaStreamCallbackTimeInfo*, 
    PaStreamCallbackFlags, void*) {
    
    std::lock_guard<std::mutex> lock(bufferMutex);
    short* out = (short*)outBuffer;
    for (int i = 0; i < frames; ++i) {
        if (!audioBuffer.empty()) {
            out[i] = audioBuffer.front();
            audioBuffer.pop();
        } else {
            out[i] = 0;
        }
    }
    return paContinue;
}

void udpReceiver() {
    WSADATA wsaData;
    WSAStartup(MAKEWORD(2,2), &wsaData);
    SOCKET sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    
    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(5000);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    bind(sock, (sockaddr*)&serverAddr, sizeof(serverAddr));

    decoder = opus_decoder_create(48000, 1, nullptr);
    char buffer[4096];
    while (true) {
        int len = recv(sock, buffer, sizeof(buffer), 0);
        short pcm[960];
        int samples = opus_decode(decoder, (uint8_t*)buffer, len, pcm, 960, 0);
        
        std::lock_guard<std::mutex> lock(bufferMutex);
        for (int i = 0; i < samples; ++i) {
            audioBuffer.push(pcm[i]);
        }
    }
}

int main() {
    Pa_Initialize();
    PaStream* stream;
    Pa_OpenDefaultStream(&stream, 0, 1, paInt16, 48000, 256, audioCallback, nullptr);
    Pa_StartStream(stream);

    std::thread(udpReceiver).detach();
    while (true) std::this_thread::sleep_for(std::chrono::hours(1));
}