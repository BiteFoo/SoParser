package com.example;

/**
 * Created by John.Lu on 2017/8/3.
 * elf常用数据类
 *
 * 根据android4.4.4 源码linker 解析elf文件
 *
 */

public class ELFDataHelper {


    public static class  elf_file_type {
        public static  final  int ET_NONE =0;
        public static  final  int ET_REL =1;
        public static  final  int ET_EXEC =2;
        public static  final  int ET_DYN =3;
        public static  final  int ET_CORE =4;
        public static  final  int ET_LOOS =0xfe00;
        public static  final  int ET_HIOS =0xfeff;
        public static  final  int ET_LOPROC =0xff00;
        public static  final  int ET_HIPROC =0xffff;
    }

    /**
     * 这里只关注三个架构 x86 arm mips
     * */
    public static  class elf_machine{
        public static  final  int EM_ARM =40;
        public static  final  int EM_MIPS =8;
        public static  final  int EM_386 =3;
    }

    public static class  elf_magic{
        public static  final  int ELFMAG0 =0x7f;
        public static  final  int ELFMAG1 =0x45;
        public static  final  int ELFMAG2 =0x4c;
        public static final  int ELFMAG3 = 0x46;
    }


    public static  class elf_version{
        public static  final int EV_NONE =0;
        public static final  int EV_CURRENT =1;

    }

}
