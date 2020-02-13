package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;

import java.util.List;

public class DecodeRunnable implements Runnable {
    private DecodeSuccessListener listener;
    private byte[] data;
    private int screenWidth;
    private int screenHeight;
    private boolean needGetScanBitmap;
    private Rect encodeRect;
    private List<BarcodeFormat> codeFormat;
    private boolean isVerticalScreen;

    public void setVerticalScreen(boolean isVerticalScreen) {
        this.isVerticalScreen = isVerticalScreen;
    }

    public void setListener(DecodeSuccessListener listener) {
        this.listener = listener;
    }

    public void setNeedGetScanBitmap(boolean needGetScanBitmap) {
        this.needGetScanBitmap = needGetScanBitmap;
    }

    public boolean isNeedGetScanBitmap() {
        return needGetScanBitmap;
    }

    public Rect getEncodeRect() {
        if (encodeRect.width() <= 0 || encodeRect.height() <= 0) {
            return null;
        }
        return encodeRect;
    }

    public void setEncodeRect(Rect encodeRect) {
        this.encodeRect = encodeRect;
    }

    public DecodeSuccessListener getListener() {
        if (listener == null) {
            listener = new DecodeSuccessListener() {
                @Override
                public void onSuccess(Result rawResult, Bitmap bitmap) {

                }

                @Override
                public void onError() {

                }
            };
        }
        return listener;
    }

    public DecodeRunnable(byte[] data, int screenW, int screenH) {
        this.data = data;
        this.screenWidth = screenW;
        this.screenHeight = screenH;
    }

    public void setCodeFormat(List<BarcodeFormat> codeFormat) {
        this.codeFormat = codeFormat;
    }

    @Override
    public void run() {
        if (data == null) {
            return;
        }
        startEncode();
    }



    private void startEncode() {
        if(data==null){
            getListener().onError();
            return;
        }
        Rect encodeRect = getEncodeRect();

        PlanarYUVLuminanceSource source = DecodeUtils.startBuildYUV(data, isVerticalScreen, screenWidth, screenHeight, encodeRect);

        Result rawResult = DecodeUtils.startEncode(source, codeFormat);

        if (rawResult != null) {
            Bitmap bitmap = null;
            if (isNeedGetScanBitmap()) {
                bitmap = DecodeUtils.getScanBitmap(source);
            }
            getListener().onSuccess(rawResult, bitmap);
        } else {
            getListener().onError();
        }

    }
}
