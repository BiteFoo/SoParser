#ifndef LOGCAT_H_
#define LOGCAT_H_  1

#include <android/log.h>

#define TAG "myjni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)


#endif /** LOGCAT_H_*/