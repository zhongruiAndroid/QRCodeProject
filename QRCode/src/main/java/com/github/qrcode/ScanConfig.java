package com.github.qrcode;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;

import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

public class ScanConfig {
    public static final int MAX_SIZE=6;

    public List<BarcodeFormat>codeFormat;
    private QRCodeListener listener;

    public int screenWidth;
    public int screenHeight;
    public int intervalFrameNum;
    public int maxFrameNum;
    public boolean isNeed;
    public Rect scanRect;


    public ScanConfig(QRCodeListener qrCodeListener) {
        maxFrameNum=MAX_SIZE;
        this.listener = qrCodeListener;
    }
    public QRCodeListener getListener() {
        return listener;
    }
    public void init() {
        Activity activity = getListener().getAct();
        if(activity==null){
            throw new IllegalStateException("QRCodeListener getAct(Activity) ctx can not null");
        }
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        screenWidth=getScreenWidth(activity);
        screenHeight=getScreenHeight(activity);
        /*
        if(configuration.orientation==Configuration.ORIENTATION_PORTRAIT){

        }else{
            screenWidth=getScreenHeight(activity);
            screenHeight=getScreenWidth(activity);
        }*/

        scanRect = getListener().getScanRect();
        if(scanRect ==null|| scanRect.width()<=0|| scanRect.height()<=0){
            scanRect=new Rect();
            scanRect.set(0,0,screenWidth,screenHeight);
        }

        /*存放最多的帧数*/
        maxFrameNum = getListener().getMaxFrameNum();
        if(maxFrameNum<=0){
            maxFrameNum=6;
        }else if(maxFrameNum>20){
            maxFrameNum=20;
        }
        /*是否需要返回识别成功时的图片*/
        isNeed = getListener().needGetBitmapForSuccess();

        codeFormat=new ArrayList<>();
        codeFormat.add(BarcodeFormat.QR_CODE);
    }

    private int getScreenWidth(Activity activity) {
        Point outSize = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealSize(outSize);
            return outSize.x;
        } else {
            int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
            return widthPixels;
        }
    }

    private int getScreenHeight(Activity activity) {
        Point outSize = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealSize(outSize);
            return outSize.y;
        } else {
            int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
            return heightPixels;
        }
    }
}
