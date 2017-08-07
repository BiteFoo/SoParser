

#ifndef ANTIDEBUG_H_
#define ANTIDEBUG_H_ 1

#include <sys/ptrace.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <android/log.h>
#include <sys/syscall.h>
#include <sys/inotify.h>
#include <pthread.h>
#include <sys/prctl.h>
#include <sys/wait.h>
#include <signal.h>
#include <errno.h>
#include <sys/queue.h>
#include <sys/select.h>
#include <stdio.h>
#include <stdlib.h>

extern void readStatus();

extern void AntiDebug();

extern void CalcTime(int, int);

void safe_attach(pid_t pid);

void handle_events();

int checkDebugger();

void checkAndroidServer();

void runInotify();

void* anti_ptrace(void* arg);

// void checkDebug();
//  return 1 is ok  return -1 should kill thread & process
int checkDebug () ;


#endif  /**  ANTIDEBUG_H_*/