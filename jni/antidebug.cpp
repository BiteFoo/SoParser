#include "antidebug.h"
#include  "logcat.h"
#define CHECK_TIME 1
#define MAX 128
#define WCHAN_ELSE 0;
#define WCHAN_RUNNING 1;
#define WCHAN_TRACING 2;
int keep_running;
pthread_t t_id;

void signal_handle(int num)  {
    keep_running = 0;
}

int getWchanStatus()  {
    char *wchaninfo = new char[128];
    int result = WCHAN_ELSE;
    char *cmd = new char[128];
    pid_t pid = syscall(__NR_getpid);
    sprintf(cmd, "cat /proc/%d/wchan", pid);
    if (cmd == NULL) {
        return WCHAN_ELSE;
    }
    FILE *ptr;
    if ((ptr = popen(cmd, "r")) != NULL) {
        if (fgets(wchaninfo, 128, ptr) != NULL) {
//            LOGE("wchaninfo= %s", wchaninfo);
        }
    }
    if (strncasecmp(wchaninfo, "sys_epoll\0", strlen("sys_epoll\0")) == 0) {
        result = WCHAN_RUNNING;
    }
    else if (strncasecmp(wchaninfo, "ptrace_stop\0", strlen("ptrace_stop\0")) == 0) {
        result = WCHAN_TRACING;
    }
    return result;
}


void CalcTime (int res, int des)  {
    int pid = getpid();
    if (des - res >= 2) {
        kill(pid, SIGKILL);
    }
}

void checkAndroidServer()  {
    char szLines[1024] = {0};
    //监听23946端口
    FILE *fp = fopen("/proc/net/tcp", "r");
    if (fp != NULL) {
        while (fgets(szLines, sizeof(szLines), fp)) {
            //23946端口
            if (strstr(szLines, "00000000:5D8A")) {
                kill(getpid(), SIGKILL);
                break;
            }
        }
        fclose(fp);
    }

   LOGI("no find android server");
}


int call_ptrace()
{
    
}

//on 7.0 os  
void readStatus()  {
    FILE *fd;
    char filename[MAX];
    char line[MAX];
    pid_t pid = syscall(__NR_getpid);
    int ret = getWchanStatus();
    if (2 == ret) {
        kill(pid, SIGKILL);
    }
    sprintf(filename, "/proc/%d/status", pid);// 读取proc/pid/status中的TracerPid
    if (fork() == 0) {
        int pt;
        pt = ptrace(PTRACE_TRACEME, 0, 0, 0); //子进程反调试
        if (pt == -1)
            exit(0);
        while (1) {
            checkAndroidServer();
            fd = fopen(filename, "r");
            while (fgets(line, MAX, fd)) {
                if (strncmp(line, "TracerPid", 9) == 0) {
                    int statue = atoi(&line[10]);
                    fclose(fd);
                    syscall(__NR_close, fd);
                    if (statue != 0) {
                        int ret = kill(pid, SIGKILL);
                        return;
                    }

                    break;
                }
            }
            sleep(CHECK_TIME);
        }
    }

   
}


int event_check(int fd)   {
    fd_set rfds;
    FD_ZERO(&rfds);
    FD_SET(fd, &rfds);

    return select(FD_SETSIZE, &rfds, NULL, NULL, NULL);
}

int read_event(int fd)   {
    char buffer[16384] = {0};
    size_t index = 0;
    struct inotify_event *ptr_event;

    ssize_t r = read(fd, buffer, 16384);
    if (r <= 0) {
       // LOGE("read_event");
        return r;
    }

    while (index < r) {
        ptr_event = (struct inotify_event *) &buffer[index];
        //此处监控事件的读和打开，如果出现则直接结束进程
        if ((ptr_event->mask & IN_ACCESS) || (ptr_event->mask & IN_OPEN)) {
            //事件出现则杀死父进程
            int ret = kill(getpid(), SIGKILL);
            return 0;
        }

        index += sizeof(struct inotify_event) + ptr_event->len;
    }
    return 0;
}

void runInotify() {
    keep_running = 1;
    pid_t ppid = syscall(__NR_getpid);
//    if (signal(SIGINT, signal_handle) == SIG_IGN) {
//        signal(SIGINT, SIG_IGN);
//    }

    int fd;
    char buf[1024];
    fd = inotify_init();//初始化
    if (fd == -1) { //错误处理
       // LOGE("inotify_init error");
        switch (errno) {
            case EMFILE:
               // LOGE("errno: EMFILE");
                break;
            case ENFILE:
            	//LOGE("errno: ENFILE");
                break;
            case ENOMEM:
            	//LOGE("errno: ENOMEM");
                break;
            default:
            	//LOGE("unkonw errno");
                break;
        }
        return;
    }

    int wd;
    sprintf(buf, "/proc/%d/maps", ppid);
    wd = inotify_add_watch(fd, buf, IN_ALL_EVENTS); //添加监视
    if (wd == -1) { //错误处理
       // LOGE("inotify_add_watch");
        switch (errno) {
            case EACCES:
                //LOGE("errno: EACCES");
                break;
            case EBADF:
               // LOGE("errno: EBADF");
                break;
            case EFAULT:
               // LOGE("errno: EFAULT");
                break;
            case EINVAL:
               // LOGE("errno: EINVAL");
                break;
            case ENOMEM:
               // LOGE("errno: ENOMEM");
                break;
            case ENOSPC:
            	//LOGE("errno: ENOSPC");
                break;
            default:
            	//LOGE("unkonw errno");
                break;
        }
        return;
    }

    while (keep_running) {
        if (event_check(fd) > 0) {
            read_event(fd);
        }
    }
    return;
}

void* anti_ptrace(void* arg) {
	readStatus();
    return NULL;
}

// void checkDebug(){
// 	if(pthread_create(&t_id, NULL, anti_ptrace, NULL) != 0){
// 		kill(getpid(), SIGKILL);
// 	}
// }
int  checkDebug(){
	LOGI("call checkDebug  started " );
    if(pthread_create(&t_id, NULL, anti_ptrace, NULL) != 0){
        kill(getpid(), SIGKILL);
        return -1;
    }
    	LOGI("call checkDebug finishedd ");
    return 1;
}


