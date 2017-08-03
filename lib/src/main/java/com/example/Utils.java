package com.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    /**
     * 将byte[] 转成int 类型
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
        int s1 = (int)data[1];
        int s2 = (int)data[2];
        int s3 = (int)data[3];
//        int s0 = data[0];
//        int s1 = data[1];
//        int s2 = data[2];
//        int s3 = data[3];
        log("s0="+s0 +" , s1="+s1+" ,s2="+s2+" ,s3="+s3);
        log("s0="+(s0 & 0xff) +" , s1="+((s1 << 8) &0xff00)+" ,s2="+((s2 << 24) >>> 8)+" ,s3="+((s3 << 24)));
        target = (s0 & 0xff) | ((s1 << 8) &0xff00)
                 | ((s2 << 24) >>> 8)
                 |((s3 << 24));
        int targets = (data[0] & 0xff)
                | ((data[1] << 8) & 0xff00)
                | ((data[2] << 24) >>> 8)
                | (data[3] << 24);
log("targets: "+targets);
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
        log("s0: "+s0);
        short s1 = (short) data[1];
        log("s1: "+s1);
        target =(short)(s0 | s1);
        log("相加数据:　"+(s0+s1));
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
            return soinfo;
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

    public static  void log(String msg)
    {
        System.out.println("[+] "+msg);
    }





}
