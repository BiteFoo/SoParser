package itsec.parserelf;

import android.util.Log;

/**
 * Created by John.Lu on 2017/8/3.
 *
 * 通用方法类
 *
 */

public class Utils {

    /**
     * 输出字符串信息
     * */
    public static String  byte2HexString(byte[] data)
    {
        String result =null;

        return  result;
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
            Log.e("utils","src is null");
            return result;
        }
        result = new byte[off];
        System.arraycopy(src,start,result,0,off);
        return  result;


    }

}
