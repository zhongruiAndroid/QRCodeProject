package com.github.qrcode;

/***
 *   created by zhongrui on 2020/2/9
 */
public class DataRotate {
    public static byte[] rotate90(byte[]originData,int originWidth,int originHeight){
        if(originData==null){
            return originData;
        }
        byte [] newData=new byte[originData.length];
        for (int y = 0; y < originHeight; y++) {
            for (int x = 0; x < originWidth; x++) {
                newData[x * originHeight + originHeight - y - 1] = originData[x + y * originWidth];
            }
        }
        return newData;
    }
}
