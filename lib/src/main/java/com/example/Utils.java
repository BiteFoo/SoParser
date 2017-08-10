package com.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by John.Lu on 2017/8/3.
 *
 * 通用方法类
 *
 */

public class Utils {


    private static  char[] sChars = "0123456789abcdef".toCharArray();
    public static String sHexStr = "0123456789abcdef";
    /**
     * 输出字符串信息
     *
     * 因为是小端模式，这里需要反序输出，例如在elf文件中有： 1234  在给用户看的： 3412
     * */
    public static String  byte2HexStringReverse(byte[] data)
    {
        StringBuffer sb = new StringBuffer();
        //反序输出  for(int index =data.length; index >=0;--index)
        for(int index =0; index < data.length;++index){
        String hexstr  = Integer.toHexString(data[index]);
            if(hexstr .length()< 2) //长度小于2 那么需要补0，例如 ，1 == 》 01
            {
             sb.append("0" +hexstr);
            }
            else
            {
                sb.append(hexstr);
            }
            sb.append(" ");
        }
        return  sb.toString();
    }

    public static  void saveFile(String path,byte[] data)
    {
        File file = new File(path+File.separator+"newMain");
        try {
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.close();

        }catch (FileNotFoundException e)
        {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static byte[] copyBytes(byte[] res, int start, int count){
        if(res == null){
            return null;
        }
        byte[] result = new byte[count];
        for(int i=0;i<count;i++){
            result[i] = res[start+i];
        }
        return result;
    }

    /**
     * 对齐算法
     *
     * 返回一个 addr的align的整数倍
     * */
    public static  int align(int addr, int align)
    {
        if(align > addr)
        {
            return addr;
        }
        int offset  = addr % align;
        return addr + (align - offset);
    }

    public static byte[] int2Byte(int num)
    {
        int tmp = num;
        byte[] buff = new byte[4];//?? 4个字节
        for(int i =0; i< 4; ++i)
        {
            buff[i] = new Integer(tmp &0xff).byteValue(); //将最低位保存在最低位
            tmp  = tmp >> 8;//向右移动8位
        }
        return  buff;
    }


    /**
     * 将byte[] 转成int 类型 4个字节
     * 针对的是 四个字节的数据类型
     *   elf32Word
     *   elf32Addr
     *   elf32Off
     *java中有三种移位运算符

     <<      :     左移运算符，num << 1,相当于num乘以2

     >>      :     右移运算符，num >> 1,相当于num除以2

     >>>    :     无符号右移，忽略符号位，空位都以0补齐

     * */
    public static  int byte2Int(byte[] data)
    {
        int target =-1;
        int s0 = (int)data[0];
        int s1 = (int)data[1]; //
        int s2 = (int)data[2];
        int s3 = (int)data[3];
//        11 0111 0000 0000  == 0011 0111 0000 0000  & 1111 1111 0000 0000 == 0011 0111 0000 0000
       // log("s1 "+Integer.toHexString(s1)+ "　，"+Integer.toBinaryString(s1));
       // log("s1 hex << 8  = "+Integer.toHexString(s1<<8) +" , binary "+ Integer.toBinaryString(s1) + "　，　＜＜８　"+Integer.toBinaryString(s1<<8) +" , oxff00 binary "+ Integer.toBinaryString(0xff00));
       // log("s0="+s0 +" , s1="+s1+" ,s2="+s2+" ,s3="+s3);
       // log("s0="+(s0 & 0xff) +" , s1="+((s1 << 8) &0xff00)+" ,s2="+((s2 << 24) >>> 8)+" ,s3="+((s3 << 24)));
        target = (s0 & 0xff) | ((s1 << 8) &0xff00)
                 | ((s2 << 24) >>> 8)
                 |((s3 << 24));

        return  target;
    }


    /**
     * 2个字节
     *  计算出对应的数据类型长度
     * 针对两个字节的数据类型
     * 例如  elf32Half
     * */
    public static  short byte2Short(byte[] data)
    {
        short target =-1;
        short s0 =(short)data[0];
      //  log("s0: "+s0);
        short s1 = (short) data[1];
       // log("s1: "+s1);
        target =(short)(s0 | s1);
        //log("相加数据:　"+(s0+s1));
        return  target;
    }


    /**
     * src : 原始数据数组 ，
     * start 从数组的哪个位置开始读取
     * off 偏移量
     * */
    public static byte[] copyByteArray(byte src[] ,int start,int off)
    {
        byte result[] = null;

        if(src == null || src.length < 0)
        {
            System.out.println("utils src is null");
            return result;
        }
        result = new byte[off];
       System.arraycopy(src,start,result,0,off);
        return  result;
    }

    /**
     * 读取So文件流
     *
     * */
    public static byte[] readElfInfo(String path)
    {
        byte[] soinfo =null;
        byte[] buf = new byte[1024];
        FileInputStream fileInputStream =null;
        ByteArrayOutputStream outputStream = null;
        File  so = new File(path);
        if(!so.exists() || !so.isFile())
        {
            log("文件不存在 ，file is not found ");
           throw  new UnknownError(path+ " 文件不存在");
        }
        try {
             fileInputStream = new FileInputStream(path);
             outputStream = new ByteArrayOutputStream();
            int size = -1;
            while ((size = fileInputStream.read(buf))!=-1)
            {
            outputStream.write(buf,0,size);
            }
            outputStream.flush();
            soinfo = outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fileInputStream != null)
            {
                try {
                    fileInputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream != null)
            {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return soinfo;
    }


    /**
     * long转化成byte
     * @param number
     * @return
     */
    public static byte[] long2ByteAry(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }


    /**
     * short转化成byte
     * @param number
     * @return
     */
    public static byte[] short2Byte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();//将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    /**
     * 替换rep_index位置的byte[]
     * @param src
     * @param rep_index
     * @param copyByte
     * @return
     */
    public static byte[] replaceByteAry(byte[] src, int rep_index, byte[] copyByte){
        for(int i=rep_index;i<rep_index+copyByte.length;i++){
            src[i] = copyByte[i-rep_index];
        }
        return src;
    }
    /**
     * 高地位互换
     */
    public static byte[] reverseBytes(byte[] bytes){
        if(bytes == null || (bytes.length % 2) != 0){
            return bytes;
        }
        int i = 0;
        int offset = bytes.length/2;
        while(i < (bytes.length/2)){
            byte tmp = bytes[i];
            bytes[i] = bytes[offset+i];
            bytes[offset+i] = tmp;
            i++;
        }
        return bytes;
    }

    public static  void log(String msg)
    {
        System.out.println("[+] "+msg);
    }





}
