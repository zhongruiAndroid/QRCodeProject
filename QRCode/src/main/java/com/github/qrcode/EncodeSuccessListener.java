package com.github.qrcode;

import android.graphics.Bitmap;

import com.google.zxing.Result;

public interface EncodeSuccessListener {
    void onSuccess(Result rawResult, Bitmap bitmap);
    void onError();
}
