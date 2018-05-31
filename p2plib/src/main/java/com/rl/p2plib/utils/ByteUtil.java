package com.rl.p2plib.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Nicky on 2017/3/28.
 */

public class ByteUtil {

    private ByteUtil(){}

//    public static byte[] int2bytes(int res) {
//        byte[] targets = new byte[4];
//
//        targets[0] = (byte) (res & 0xff);// 最低位
//        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
//        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
//        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
//        return targets;
//    }
//
//    public static int byte2int(byte[] res) {
//        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
//        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
//                | ((res[2] << 24) >>> 8) | (res[3] << 24);
//        return targets;
//    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     */
    public static byte[] intToBytes( int value )
    {
//        byte[] src = new byte[4];
//        src[3] =  (byte) ((value>>24) & 0xFF);
//        src[2] =  (byte) ((value>>16) & 0xFF);
//        src[1] =  (byte) ((value>>8) & 0xFF);
//        src[0] =  (byte) (value & 0xFF);
//        return src;
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value)
    {
//        byte[] src = new byte[4];
//        src[0] = (byte) ((value>>24) & 0xFF);
//        src[1] = (byte) ((value>>16)& 0xFF);
//        src[2] = (byte) ((value>>8)&0xFF);
//        src[3] = (byte) (value & 0xFF);
//        return src;
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     */
    public static int bytesToInt(byte[] src, int offset) {
//        int value;
//        value = (int) ((src[offset] & 0xFF)
//                | ((src[offset+1] & 0xFF)<<8)
//                | ((src[offset+2] & 0xFF)<<16)
//                | ((src[offset+3] & 0xFF)<<24));
//        return value;
//        ByteBuffer.wrap(src).order(ByteOrder.LITTLE_ENDIAN).getInt();
        return  ByteBuffer.wrap(src,offset,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
//        int value;
//        value = (int) ( ((src[offset] & 0xFF)<<24)
//                |((src[offset+1] & 0xFF)<<16)
//                |((src[offset+2] & 0xFF)<<8)
//                |(src[offset+3] & 0xFF));
//        return value;
//        ByteBuffer.wrap(src).order(ByteOrder.BIG_ENDIAN).getInt();
        return ByteBuffer.wrap(src,offset,4).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public static String byte2hexStr(byte [] buffer , String split, int len){
        String h = "";
        for(int i = 0; i < len; i++){
//            XLog.w("byte2hexStr",String.format("buff[%d]: %x",i,buffer[i]));
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + split + temp;
        }
        return h;
    }

    /** byte 数组打印 */
    public static String byte2hexStr(byte [] buffer , String split){
        return byte2hexStr( buffer,split,buffer.length );
    }



    public static boolean isMatched( byte [] src, int srcPos, byte [] dest, int destPos, int length ){

        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (dest == null) {
            throw new NullPointerException("dest == null");
        }
        if (srcPos < 0 || destPos < 0 || length < 0 ||
                srcPos > src.length - length || destPos > dest.length - length) {
            throw new ArrayIndexOutOfBoundsException(
                    "src.length=" + src.length + " srcPos=" + srcPos +
                            " dest.length=" + dest.length + " destPos=" + destPos + " length=" + length);
        }

        for (int i = 0; i < length; ++i) {
            if( dest[destPos + i] != src[srcPos + i] ){
                return false;
            }
        }
        return true;
    }



}
