#include <jni.h>
#include <opus/opus.h>

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_timer_audiostreamer_OpusWrapper_initEncoder(
    JNIEnv *env, jobject thiz, jint sample_rate, jint channels) {
    int err;
    return (jlong) opus_encoder_create(sample_rate, channels, OPUS_APPLICATION_AUDIO, &err);
}

JNIEXPORT jbyteArray JNICALL
Java_com_timer_audiostreamer_OpusWrapper_encode(
    JNIEnv *env, jobject thiz, jlong encoder, jshortArray pcm_data, jint frame_size) {
    opus_encoder *enc = (opus_encoder *) encoder;
    jshort *pcm = env->GetShortArrayElements(pcm_data, nullptr);
    unsigned char output[4096];
    int len = opus_encode(enc, pcm, frame_size, output, 4096);
    env->ReleaseShortArrayElements(pcm_data, pcm, JNI_ABORT);

    jbyteArray result = env->NewByteArray(len);
    env->SetByteArrayRegion(result, 0, len, (jbyte *) output);
    return result;
}
}