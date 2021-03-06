package com.github.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.List;

public interface QRCodeListener {
    Activity getAct();
    Rect getScanRect();
    int getMaxFrameNum();
    boolean needGetBitmapForSuccess();
    void onSuccess(Result rawResult, Bitmap bitmap);
    List<BarcodeFormat> getCodeFormat();
}
