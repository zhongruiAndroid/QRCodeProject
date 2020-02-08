package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.List;

public class EncodeRunnable implements Runnable {
    private EncodeSuccessListener listener;
    private byte[] data;
    private int screenWidth;
    private int screenHeight;
    private boolean needGetScanBitmap;
    private Rect encodeRect;
    private List<String> codeFormat;

    public void setListener(EncodeSuccessListener listener) {
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

    public EncodeSuccessListener getListener() {
        if (listener == null) {
            listener = new EncodeSuccessListener() {
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

    public EncodeRunnable(byte[] data, int screenW, int screenH) {
        this.data = data;
        this.screenWidth = screenW;
        this.screenHeight = screenH;
    }
    public void setCodeFormat(List<String> codeFormat){
        this.codeFormat=codeFormat;
    }
    @Override
    public void run() {
        if (data == null) {
            return;
        }
        startEncode();
    }

    private void startEncode() {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setCodeFormat(codeFormat);
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, screenHeight, screenWidth);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                multiFormatReader.reset();
            }
        }
        if (rawResult != null) {
            Bitmap bitmap = null;
            if (isNeedGetScanBitmap()) {
                bitmap = bundleThumbnail(source);
            }
            getListener().onSuccess(rawResult, bitmap);
        }else{
            getListener().onError();
        }
    }

    /*width：横屏时的宽度
    * height：横屏的高度
    * */
    private PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = new Rect(getEncodeRect());
        if (rect == null) {
            rect = new Rect(0, 0, width, height);
        } else {
            if (rect.width() > height) {
                int top = rect.top;
                int bottom = rect.bottom;
                rect.set(0, top, height, bottom);
            }
            if (rect.height() > width) {
                int left = rect.left;
                int right = rect.right;
                rect.set(left, 0, right, width);
            }
            int left = rect.left;
            int top = rect.top;
            int right = rect.right;
            int bottom = rect.bottom;

            //因为摄像头默认横向,这里相对横向坐标转换位置
            rect.set(top, height - right, bottom,height - left);
        }
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
    }

    private Bitmap bundleThumbnail(PlanarYUVLuminanceSource source) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);

        Matrix matrix=new Matrix();
        matrix.setRotate(90);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, true);

      /*  Bitmap newBitmap=Bitmap.createBitmap(height,width, Bitmap.Config.RGB_565);
        Canvas canvas=new Canvas(newBitmap);
        canvas.rotate(90,width/2,height/2);
        canvas.drawBitmap(bitmap,matrix,null);*/

//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);

//        byte[] compressedBitmap = out.toByteArray();
//        float scaleFactor = (float) width / source.getWidth();
        /*Bitmap barcode = null;
        if (compressedBitmap != null) {
            barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
            barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
        }*/
        return newBitmap;
    }

}