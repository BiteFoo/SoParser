package com.example;

/**
 * Created by John.Lu on 2017/8/10.
 *
 * 添加一个section
 * 在当前的section基础上添加一个
 *
 *思路：
 *  读取section_hdr的elf32_shoff ,找到section表的偏移地址 ，
 *
 *
 *
 *
 */

public class AddSection {

    //*********************当做一个去全新的section添加
    private final static String newSectionName = ".hya";
    private final static int newSectionSize = 1000;
    private final static int newSectionNameLen = 0x10;//new section name的长度不能超过0x10

    private final static int sectionSize = 40;//一个Section的大小
    private final static int stringSectionSizeIndex = 20;//String section中的size字段的index
    private final static int programFileSizeIndex = 16;//program header中的file size的index
    private final static int elfHeaderSize = 0x34;//elf header的大小
    private final static int programHeaderSize = 0x20;//Program Header的大小
    private final static int elfHeaderSectionCountIndex = 48;//elf header中的section总数
//
//    public static int sectionHeaderOffset;//section header的偏移值
//    public static short stringSectionInSectionTableIndex;//string section在section list中的index
//    public static int stringSectionOffset;//string section中的偏移值
//    public static int firstLoadInPHIndex;//第一个Load类型的Program Header的在Program Header List中的index 从0开始
//    public static int lastLoadInPHIndex;
//    public static int addSectionStartAddr = 0;//添加Section段的开始地址

    //**********************
    public  int sectionHeaderOffset;//节区表的偏移首地址
    public  int stringSectionInSectionIndex;//字符串节区在节区表中的索引位置
    public  int stringSectionOffset;//字符串节区的偏移地址
    //程序表的第一个可加载段的索引
    public int firstLoadIndexInPhdr;
    //程序表的最后一个可加载表的位置索引
    public int lastLoadIndexInPhdr;
    //
    public  int addSectionStartAddr ;//添加的新节区的地址

    /**
     *  public byte[] sh_name = new byte[4];
     public byte[] sh_type = new byte[4];
     public byte[] sh_flags = new byte[4];
     public byte[] sh_addr = new byte[4];
     public byte[] sh_offset = new byte[4];
     public byte[] sh_size = new byte[4];
     public byte[] sh_link = new byte[4];
     public byte[] sh_info = new byte[4];
     public byte[] sh_addralign = new byte[4];
     public byte[] sh_entsize = new byte[4];
     */
    public byte[] addSectionHeader(byte soinfo[])
    {

        byte[] newHeader = new byte[sectionSize];
        newHeader = Utils.replaceByteAry(newHeader,0,Utils.int2Byte(addSectionStartAddr - stringSectionOffset));
        newHeader = Utils.replaceByteAry(newHeader, 4, Utils.int2Byte(ELF32.SH_PROGBITS));//type=PROGBITS
        newHeader = Utils.replaceByteAry(newHeader, 8, Utils.int2Byte(ELF32.SH_ALLOC));
        newHeader = Utils.replaceByteAry(newHeader, 12, Utils.int2Byte(0x5010));
        newHeader = Utils.replaceByteAry(newHeader, 16, Utils.int2Byte(0x5010));
        newHeader = Utils.replaceByteAry(newHeader, 20, Utils.int2Byte(newSectionSize));
        newHeader = Utils.replaceByteAry(newHeader, 24, Utils.int2Byte(0));
        newHeader = Utils.replaceByteAry(newHeader, 28, Utils.int2Byte(0));
        newHeader = Utils.replaceByteAry(newHeader, 32, Utils.int2Byte(4));
        newHeader = Utils.replaceByteAry(newHeader, 36, Utils.int2Byte(0));
        //在末尾增加Section
        byte[] newSrc = new byte[soinfo.length + newHeader.length];
        newSrc = Utils.replaceByteAry(newSrc, 0, soinfo);
        newSrc = Utils.replaceByteAry(newSrc, soinfo.length, newHeader);
        return  newSrc;
    }

    public byte[] addNewSectionForFileEnd(byte soinf[])
    {
        byte[] stringByte = newSectionName.getBytes();
        byte[] newSection = new byte[newSectionSize + newSectionNameLen];
        newSection = Utils.replaceByteAry(newSection, 0, stringByte);
        //新建一个byte[]
        byte[] newSrc = new byte[0x5000 + newSection.length];
        newSrc = Utils.replaceByteAry(newSrc, 0, soinf);//复制之前的文件src
        newSrc = Utils.replaceByteAry(newSrc, addSectionStartAddr, newSection);//复制section
        return newSrc;
    }
    public byte[] changeStrtabLen(byte[] soinfo)
    {
        //获取到String的size字段的开始位置 stringSectionInSectionTableIndex
        int size_index = sectionHeaderOffset + (stringSectionInSectionIndex)*sectionSize + stringSectionSizeIndex;

        //多了一个Section Header + 多了一个Section的name的16个字节
        byte[] newLen_ary = Utils.int2Byte(addSectionStartAddr - stringSectionOffset + newSectionNameLen);
        soinfo = Utils.replaceByteAry(soinfo, size_index, newLen_ary);
        return soinfo;
    }

    public  byte[] changeElfHeaderSectionNum(byte[] soinfo)
    {
        byte[] count = Utils.copyBytes(soinfo, elfHeaderSectionCountIndex, 2);
        short counts = Utils.byte2Short(count);
        counts++;
        count = Utils.short2Byte(counts);
        soinfo = Utils.replaceByteAry(soinfo, elfHeaderSectionCountIndex, count);
        return soinfo;
    }
    public byte[] changeProgramHeaderLaodInfo(byte soinfo[])
    {
        //寻找到LOAD类型的Segement位置
        int offset = elfHeaderSize + programHeaderSize * firstLoadIndexInPhdr + programFileSizeIndex;
        //file size字段
        byte[] fileSize = Utils.int2Byte(soinfo.length);
        soinfo = Utils.replaceByteAry(soinfo, offset, fileSize);
        //mem size字段
        offset = offset + 4;
        byte[] memSize = Utils.int2Byte(soinfo.length);
        soinfo = Utils.replaceByteAry(soinfo, offset, memSize);
        //flag字段
        offset = offset + 4;
        byte[] flag = Utils.int2Byte(7);
        soinfo = Utils.replaceByteAry(soinfo, offset, flag);
        return soinfo;
    }



}
