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

    public void setCodeFormat(List<String> codeFormat) {
        this.codeFormat = codeFormat;
    }

    @Override
    public void run() {
        if (data == null) {
            return;
        }
        startEncode();
    }

    private byte[] test(byte[] mData, int imageWidth, int imageHeight) {
        byte[] data=  new byte[mData.length];
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    data[x * imageHeight + imageHeight - y - 1] = mData[x + y * imageWidth];
                }
            }
        return data;
    }
    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    private void startEncode() {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setCodeFormat(codeFormat);
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(test(data,screenHeight,screenWidth),screenWidth,  screenHeight);
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
        } else {
            getListener().onError();
        }
    }

    /*width：横屏时的宽度
     * height：横屏的高度
     * */
    private PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = new Rect(getEncodeRect());
        if (rect == null) {
            rect = new Rect(0, 0,   height,width);
        }
        /*if (rect == null) {
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
            rect.set(top, height - right, bottom, height - left);
        }*/
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
    }

    private Bitmap bundleThumbnail(PlanarYUVLuminanceSource source) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);

        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

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
