#include <jni.h>
#include <string>
#include <android/log.h>

void test1()__attribute__((construction)){
        __android_log_print(1,"ubuntu","construction");
}

void test2()__attribute__((destruction)){
__android_log_print(2,"ubuntu","destruction");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_itsec_parserelf_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
