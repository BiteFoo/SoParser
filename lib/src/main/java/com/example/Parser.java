package com.example;

/**
 * Created by John.Lu on 2017/8/3.
 */

public class Parser {

    private ELF32 elf32;
    private ELFDataHelper elfDataHelper;
    private byte[]  mSoInfo=null;
    ELF32.elf32_hdr elf32_hdr = null;
    public Parser(byte[] soInfo )
    {
        elf32 = new ELF32();
        elfDataHelper = new ELFDataHelper();
        mSoInfo = soInfo;
    }
    /**
     * 解析elf文件头
     * */
    public   void parseElfHeader()
    {
        Utils.log("===========parse elfheader===================");

        if(!checkSoInfo())
        {
            return;
        }
        elf32_hdr = new ELF32.elf32_hdr();
        elf32_hdr.e_ident = Utils.copyByteArray(mSoInfo,0,16);
        elf32_hdr.e_type = Utils.copyByteArray(mSoInfo,16,2);
        elf32_hdr.e_machine = Utils.copyByteArray(mSoInfo,18,2);

        elf32_hdr.e_version = Utils.copyByteArray(mSoInfo,20,4);
        elf32_hdr.e_entry = Utils.copyByteArray(mSoInfo,24,4);
        elf32_hdr.e_phoff = Utils.copyByteArray(mSoInfo,28,4);
        elf32_hdr.e_shoff = Utils.copyByteArray(mSoInfo,32,4);
        elf32_hdr.e_flags = Utils.copyByteArray(mSoInfo,36,4);
        elf32_hdr.e_ehsize = Utils.copyByteArray(mSoInfo,40,2);
        elf32_hdr.e_phentsize = Utils.copyByteArray(mSoInfo,42,2);
        elf32_hdr.e_phnum = Utils.copyByteArray(mSoInfo,44,2);
        elf32_hdr.e_shentsize = Utils.copyByteArray(mSoInfo,46,2);
        elf32_hdr.e_shnum = Utils.copyByteArray(mSoInfo,48,2);
        elf32_hdr.e_shstrndx = Utils.copyByteArray(mSoInfo,50,2);
        //输出对应的16进制字符串信息
        Utils.log("elfheader :\n"+elf32_hdr.toString());
    }

    /**
     * 读取elf32_phdr的信息  程序头表解析
     * 根据elf32_ehdr 得到的头部信息 e_phoff e_phnum == 得到偏移量和programm header的每一项
     * */
    public void  parseElf32_phdr()
    {
        //计算个数
        int num = Utils.byte2Short(elf32_hdr.e_phnum);//统计处programm header table 有多少个item
        Utils.log("共有 " + num + " 个 programm header ");
        int e_phoff  = Utils.byte2Int( elf32_hdr.e_phoff);
        Utils.log("e_phoff ="+e_phoff);
    }

    /**
     * 解析section header 表
     * */
    public void parseSectionHeader()
    {
        int shnum = Utils.byte2Short(elf32_hdr.e_shnum);//计算处section的数据值
        Utils.log("共有 "+ shnum+ " 个 section");
        int e_shoff  = Utils.byte2Int( elf32_hdr.e_shoff);
        Utils.log("e_shoff ="+e_shoff);
    }



    /**
     *
     * 检测函数
     * */
    private  boolean checkSoInfo()
    {
        if(mSoInfo == null || mSoInfo.length < 0)
        {
            Utils.log("soinf is null ");
            return false;
        }
        return true;
    }



}
