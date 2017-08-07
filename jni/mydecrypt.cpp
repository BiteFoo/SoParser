#include "mydecrypt.h"
#include <stdio.h>  //fopen ,fgets()
#include <unistd.h>  //mprotexct()  pagesize = getpagesize ()
#include <sys/mman.h>  //mmap()h
#include <stdlib.h> //strtoul()
#include <string.h> //strlen() srtok() 
#include "logcat.h"


/**
get target .so file in the memory ,
1.getpit()
2.read maps
3.find target .so 
4.get name  and transformed string to unsigned long ,return it .

libsecidea.so 
*/
unsigned long getLibAddr(){
	unsigned long ret = 0;
	char name[] ="libnative-lib.so";
	//according to  the pid to get the target pid, where the target file in the mmap  
	char buf[4096],*tmp;
	int pid;//target process' pid
	FILE *fp;
	pid=getpid();
	sprintf(buf,"/proc/%d/maps",pid);
	fp=fopen(buf,"r");
	if(fp == NULL)
	{
		LOGI("fopen file %s failed",buf);
		goto _error;
	}
	while(fgets(buf,sizeof(buf),fp))
	{
		if(strstr(buf,name)){
			tmp=strtok(buf,"_");
			ret=strtoul(tmp,NULL,16);
			break;
		}
	}
	_error:
	fclose(fp);
	return ret;
}

int decrypt_section()
{
   int ok =1;

	LOGI("begin to decrypt mem ");
	char name[15];
	unsigned int nblock;
	unsigned int psize;
	unsigned  long  so_base;
	unsigned long text_addr;
	unsigned int i;
	Elf32_Ehdr *ehdr; //elf header
	Elf32_Shdr *shdr; //section header 
	so_base=getLibAddr();
	ehdr=(Elf32_Ehdr *)so_base;
	//get encrypted code areas;  //得到待解密节占用的页的大小
	text_addr = ehdr->e_shoff+so_base;//get section header addr
	LOGI("get section header addr so_base =%lx",text_addr);
	nblock=ehdr->e_entry;
	psize=ehdr->e_shoff /4096 +(ehdr->e_shoff %4096  == 0 ? 0:1);
	LOGI("get encrypt pagesize psize = %x",psize);
	LOGI("check privillege ");   
	int pagesize = getpagesize ();
	//mprotect修改权限是以页为单位的，所以这里必须将起始地址设置为PAGE_SIZE的整数倍
	if(mprotect((void*)(text_addr / pagesize *pagesize),4096 * psize,PROT_READ|PROT_WRITE|PROT_EXEC != 0)) //
	{
		LOGI("mprotect can't get privillege !!");
		ok =-1;
	}
	
	//begin to decrypt
	for(i=0;i<nblock;i++)
	{
		char *addr=(char*)(text_addr + i);
		*addr = *addr ^ 11;
	}
	if(mprotect((void*)(text_addr / pagesize *pagesize),4096 * psize,PROT_READ|PROT_WRITE|PROT_EXEC != 0))
	{
		LOGI(" mprotect can't get privillege !!");
		ok =-1;
	}
	LOGI(" decrypt success !! ^_^ ");
 return ok;

}
