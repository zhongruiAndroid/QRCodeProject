package com.github.qrcode;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/***
 *   created by zhongrui on 2020/2/11
 */
public class CreateConfig {
    public ErrorCorrectionLevel errorCorrection= ErrorCorrectionLevel.H;
    private String characterSet="utf-8";
    private int margin=3;
    private int qrVersion=3;
    private int codeSize=800;

    private int foregroundColor= Color.BLACK;
    private int backgroundColor=Color.WHITE;

    private int iconMargin =0;
    private int iconWidth=0;
    private int iconCorner=0;
    private int iconImageCorner=0;


    private int iconForegroundColor=Color.TRANSPARENT;
    private int iconBackgroundColor=Color.WHITE;

    private BarcodeFormat codeFormat=BarcodeFormat.QR_CODE;

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        if(margin<0){
            margin=0;
        }
        this.margin = margin;
    }

    public int getQrVersion() {
        return qrVersion;
    }

    public void setQrVersion(int qrVersion) {
        if(qrVersion<1){
            qrVersion=1;
        }else if(qrVersion>40){
            qrVersion=40;
        }
        this.qrVersion = qrVersion;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(@ColorInt int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getIconMargin() {
        return iconMargin;
    }

    public void setIconMargin(int iconMargin) {
        if(iconMargin<0){
            iconMargin=0;
        }
        this.iconMargin = iconMargin;
    }

    public int getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
    }


    public int getIconCorner() {
        return iconCorner;
    }

    public void setIconCorner(int iconCorner) {
        this.iconCorner = iconCorner;
    }

    public int getIconImageCorner() {
        return iconImageCorner;
    }

    public void setUserIconImageCorner() {
        this.iconImageCorner = -1;
    }
    public void setIconImageCorner(int iconImageCorner) {
        this.iconImageCorner = iconImageCorner;
    }

    public int getIconForegroundColor() {
        return iconForegroundColor;
    }

 /*   public void setIconForegroundColor(@ColorInt int iconForegroundColor) {
        this.iconForegroundColor = iconForegroundColor;
    }*/

    public int getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    public void setIconBackgroundColor(@ColorInt int iconBackgroundColor) {
        this.iconBackgroundColor = iconBackgroundColor;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public BarcodeFormat getCodeFormat() {
        return codeFormat;
    }

    public void setCodeFormat(BarcodeFormat codeFormat) {
        if(codeFormat==null){
            codeFormat=BarcodeFormat.QR_CODE;
        }
        this.codeFormat = codeFormat;
    }
}
