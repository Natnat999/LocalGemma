#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "GemmaChat"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_gemmachat_llm_LlamaJNI_getVersion(JNIEnv *env, jclass /* clazz */) {
    return env->NewStringUTF("GemmaChat Native v1.0");
}

JNIEXPORT jboolean JNICALL
Java_com_gemmachat_llm_LlamaJNI_isLoaded(JNIEnv *env, jclass /* clazz */) {
    return JNI_TRUE;
}

} // extern "C"