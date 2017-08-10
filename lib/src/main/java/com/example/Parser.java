package com.example;

import java.util.ArrayList;
import java.util.List;

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


    List<ELF32.elf32_phdr> elf32_phdrList = null;
    public void  parseElf32_phdr()
    {
        int e_phentsize =32;
        elf32_phdrList = new ArrayList<>();
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
            //解析elf32_phdr
//            parseElf32_phdr(phdr);
            elf32_phdrList.add(parseElf32_phdr(phdr));
        }

        if(elf32_phdrList != null && elf32_phdrList.size() > 0)
        {
            for(ELF32.elf32_phdr elf32_phdr: elf32_phdrList)
            {
                Utils.log("program: "+elf32_phdr.toString());
            }
        }
    }
    /**
     *
     * 根据读取到的数据，解析处e_phdr的每一个数据单元大小
     *    *typedef struct elf32_phdr{
     Elf32_Word    p_type;
     Elf32_Off p_offset;
     Elf32_Addr    p_vaddr;
     Elf32_Addr    p_paddr;
     Elf32_Word    p_filesz;
     Elf32_Word    p_memsz;
     Elf32_Word    p_flags;
     Elf32_Word    p_align;
     } Elf32_Phdr;
     * */
    private ELF32.elf32_phdr parseElf32_phdr(byte[] e_phdr){

        ELF32.elf32_phdr elf32_phdr = new ELF32.elf32_phdr();
        elf32_phdr.p_type =Utils.copyByteArray(e_phdr,0,4);
        elf32_phdr.p_off =  Utils.copyByteArray(e_phdr,4,4);
        elf32_phdr.p_vaddr = Utils.copyByteArray(e_phdr,8,4);
        elf32_phdr.p_paddr = Utils.copyByteArray(e_phdr,12,4);
        elf32_phdr.p_filesz = Utils.copyByteArray(e_phdr,16,4);
        elf32_phdr.p_memsz = Utils.copyByteArray(e_phdr,20,4);
        elf32_phdr.p_flags =  Utils.copyByteArray(e_phdr,24,4);
        elf32_phdr.p_align =  Utils.copyByteArray(e_phdr,28,4);
        return elf32_phdr;
    }


    /**
     * 解析section header 表
     * */
    List<ELF32.elf32_shdr> elf32_shdrList = null;
    public void parseSectionHeader()
    {
        int section_header_size = 40;
        elf32_shdrList = new ArrayList<>();
        int shnum = Utils.byte2Short(elf32_hdr.e_shnum);//计算处section的数据值
        Utils.log("共有 "+ shnum+ " 个 section");
        int e_shoff  = Utils.byte2Int( elf32_hdr.e_shoff);
        Utils.log("e_shoff ="+e_shoff);
        byte shdr[] = new byte[section_header_size];
        for(int i =0; i< shnum;++i)
        {
            System.arraycopy(mSoInfo,i*section_header_size+e_shoff,shdr,0,section_header_size);
            elf32_shdrList.add(parseSectionHeade(shdr));
        }
        if(elf32_shdrList!= null && elf32_shdrList.size() >0)
        {
            for(ELF32.elf32_shdr elf32_shdr:elf32_shdrList ) {
                Utils.log("section : " + elf32_shdr.toString());
            }
        }
    }
    /**
     *
     *  * typedef struct elf32_shdr {
     Elf32_Word    sh_name;
     Elf32_Word    sh_type;
     Elf32_Word    sh_flags;
     Elf32_Addr    sh_addr;
     Elf32_Off sh_offset;
     Elf32_Word    sh_size;
     Elf32_Word    sh_link;
     Elf32_Word    sh_info;
     Elf32_Word    sh_addralign;
     Elf32_Word    sh_entsize;
     } Elf32_Shdr;
     * */

    private ELF32.elf32_shdr parseSectionHeade(byte[] shdr)
    {
        ELF32.elf32_shdr elf32_shdr = new ELF32.elf32_shdr();
        elf32_shdr.sh_name = Utils.copyByteArray(shdr,0,4);
        elf32_shdr.sh_type = Utils.copyByteArray(shdr,4,4);
        elf32_shdr.sh_flags= Utils.copyByteArray(shdr,8,4);
        elf32_shdr.sh_addr =Utils.copyByteArray(shdr,12,4);
        elf32_shdr.sh_offset =  Utils.copyByteArray(shdr,16,4);
        elf32_shdr.sh_size = Utils.copyByteArray(shdr,20,4);
        elf32_shdr.sh_link = Utils.copyByteArray(shdr,24,4);
        elf32_shdr.sh_info = Utils.copyByteArray(shdr,28,4);
        elf32_shdr.sh_addralign =Utils.copyByteArray(shdr,32,4);
        elf32_shdr.sh_entsize = Utils.copyByteArray(shdr,36,4);
        return  elf32_shdr;
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
           throw new UnknownError("Oops ~! soinf is null  ");
        }
        return true;
    }

/**
 * 添加新的节区
 *
 * */
    public void addNewSection(String path)
    {

        Utils.log("");
        Utils.log("****************************add new section************************************");

        AddSection addSection = new AddSection();
        addSection.sectionHeaderOffset = Utils.byte2Int(elf32_hdr.e_shoff);
        addSection.stringSectionInSectionIndex = Utils.byte2Short(elf32_hdr.e_shstrndx);
        addSection.stringSectionOffset = Utils.byte2Int(elf32_shdrList.get(addSection.stringSectionInSectionIndex).sh_offset);

        //查找programHeader 中的LOAD段 第一个和最后一个位置 ,分别记录下
        boolean flag =true;
        for (int i=0;i<elf32_phdrList.size();i++)
        {

            //在程序表中，LOAD段的值是 1 具体可以查看定义
            if(Utils.byte2Int(elf32_phdrList.get(i).p_type) == 1)
            {
                if(flag)
                {
                    addSection.firstLoadIndexInPhdr =i;//记录下第一次出现的位置;
                    flag =false;
                }
                else
                {
                    addSection.lastLoadIndexInPhdr =i;
                }
            }
        }
        //开始计算添加的位置 找到最后后一个LOAD的地址 计算方式  addr = p_offset + p_memsz

        int lastLoadVaddr = Utils.byte2Int(elf32_phdrList.get(addSection.lastLoadIndexInPhdr).p_vaddr);
        int lastLoadMemsz = Utils.byte2Int(elf32_phdrList.get(addSection.lastLoadIndexInPhdr).p_memsz);
        int lastLoadAlign = Utils.byte2Int(elf32_phdrList.get(addSection.lastLoadIndexInPhdr).p_align);

        addSection.addSectionStartAddr = Utils.align(lastLoadVaddr+lastLoadMemsz,lastLoadAlign);
        Utils.log("新添加的section的起始地址："+Utils.byte2HexStringReverse(Utils.int2Byte(addSection.addSectionStartAddr)));
        //写文件 添加数据，最后回写数据
        mSoInfo = addSection.addSectionHeader(mSoInfo);
        mSoInfo = addSection.addNewSectionForFileEnd(mSoInfo);
        mSoInfo = addSection.changeStrtabLen(mSoInfo);
        mSoInfo = addSection.changeElfHeaderSectionNum(mSoInfo);
        mSoInfo = addSection.changeProgramHeaderLaodInfo(mSoInfo);

        //
        Utils.saveFile(path,mSoInfo);




    }


}
