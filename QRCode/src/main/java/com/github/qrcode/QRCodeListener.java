package com.github.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.zxing.Result;

public interface QRCodeListener {
    Activity getAct();
    Rect getScanRect();
    int getMaxFrameNum();
    boolean needGetBitmapForSuccess();
    void onSuccess(Result rawResult, Bitmap bitmap);
}
