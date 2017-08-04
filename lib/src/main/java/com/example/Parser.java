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
     *   //根据使用010分析so文件，发现，其在elf_header中的s_phentsize = 32(10进制)  e_shentsize = 40() 正常so，未作处理
     *   http://blog.csdn.net/jiangwei0910410003/article/details/49336613/
     * 读取elf32_phdr的信息  程序头表解析
     * 根据elf32_ehdr 得到的头部信息 e_phoff e_phnum == 得到偏移量和programm header的每一项
     * */


    public void  parseElf32_phdr()
    {
        int e_phentsize =32;

        //计算个数
        int num = Utils.byte2Short(elf32_hdr.e_phnum);//统计处programm header table 有多少个item
        Utils.log("共有 " + num + " 个 programm header ");
        int e_phoff  = Utils.byte2Int( elf32_hdr.e_phoff);//
        Utils.log("e_phoff ="+e_phoff);

        byte[] phdr = new byte[e_phentsize];

        for(int i =0;i< num;++i)
        {
            /**
             *
             * 计算方式： 从读取的soinfo中得到数据，根据e_phentsize 大小=32 ，每个phdr的大小都是一样。 从当前偏移地址依次计算每个读取位置
             *  假设有n个program header table  ,   off 是当前的phdr距离elf_header的偏移地址 ，e_phentsize 是每个表的大小
             *
             *  则：
             *
             *  每个phdr的item项
             *   phdr_i_data_off = i * e_phentsize+off (0<=i < n);
             *   总的每个表需要读取的数据为
             *   byte[] data = new byte[e_phentsize];//这个是读取数据缓冲区
             *
             *   System.arraycopy(soinfo,phdr_i_data_off,data,0,e_phentsize);//
             *
             *
             * */
            System.arraycopy(mSoInfo,i*e_phentsize+e_phoff,phdr,0,e_phentsize);// System.arraycopy(mSoInfo,i*header_size+e_phoff,phdr,0,header_size)
        }
    }
    /**
     *
     * 根据读取到的数据，解析处e_phdr的每一个数据单元大小
     * */
    private ELF32.elf32_phdr parseElf32_phdr(byte[] e_phdr){

        ELF32.elf32_phdr elf32_phdr = new ELF32.elf32_phdr();
        return null;
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
