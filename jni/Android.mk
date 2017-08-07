#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := myjni.cpp   antidebug.cpp

# LOCAL_CFLAGS += -Wl,-init=my_init

#-mllvm -sub -mllvm -fla -mllvm -bcf
# LOCAL_CFLAGS := -O0  -mllvm -sub
# LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -O0  

LOCAL_LDLIBS :=-llog
include $(BUILD_SHARED_LIBRARY)
