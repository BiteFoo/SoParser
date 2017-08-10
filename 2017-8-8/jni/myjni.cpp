//
// Created by John.Lu on 2017/8/7.
//测试jni JNI_OnLoad函数和 __attribute__((construction)) 的执行时机

// https://github.com/BiteFoo/SoParser  android project
//

#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "logcat.h"
// #include <string>
#include "antidebug.h"
#include <assert.h>

#include "mydecrypt.h"//add decrypt function

#define  MY_JAVA_METHOD "itsec/parserelf/MainActivity/stringFromJNI"
#define MY_JAVA_CLASS  "itsec/parserelf/MainActivity"


#define DECRYPT_OK  0

// using namespace std;

void function()//__attribute__((section(".hya")))
{
    LOGI("call function  .. ok ");
}

void test()__attribute__((constructor)){
    LOGI("test construction ... ============ >>>  ");
   
  if(checkDebug() == 1)
  {
  if( decrypt_section())
  {
     LOGE("call fuction  <<<***************>>> ");
   //function();//call encrypted function
  }

  }

}



//提供给java调用
jstring native_show(JNIEnv *env, jobject object) {
    LOGI("call native_show method");
    char *msg = "test my jni";
    //std::string mValue = "std::string mValue;";
    jstring value = env->NewStringUTF(msg);
    return value;
}
//提供注册数组表 方法注册表
static JNINativeMethod gMethods[] = {
        {"stringFromJNI", "()Ljava/lang/String;", (void *)&native_show},
};
static int registerNativeMethods(JNIEnv *env) //这里的env 指针是一个入口调用，
{
    jclass clazz;
    LOGI("call registerNativeMethods");
    clazz = env->FindClass(MY_JAVA_CLASS);//找到对象
    if (clazz == NULL) {
        LOGI("oops!! clazz is null");
        return JNI_FALSE;
    }
    //
    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        LOGI("Oops !@ env->RegisterNatives is failed ");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}
//实现自己的JNI_OnLoad
// __attribute__((section(".hya")))
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *resereved) __attribute__((section(".hya"))){
    JNIEnv *env;
    jint result = -1;
    LOGI("call JNI_OnLoad DECRYPT_OK =  %d ",DECRYPT_OK);
    // if(checkDebug() == -1) //  这里被第二次调用会崩掉，根据查到的函数提示 ，readStatus()和runIntify()函数出现了内存错误 ，
    //修正方法，不在这里调用函数
    //选择在init_array段进行调用反调试 ，

    // {
    //     LOGI("call JNI_OnLoad  checkDebug failed  ");
    //     return result;
    // }
    LOGE("call fuction  *************** ");
   function();//call encrypted function
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGI("Oops !@ env->JNI_Onload is failed ");
        return result;
    }
    //assert(env);
    if (!registerNativeMethods(env)) {
        LOGI(" JNI_Onload  Oops !@ registerNativeMethods failed ");
        return result;
    }
    result = JNI_VERSION_1_4;
    return result;

}

