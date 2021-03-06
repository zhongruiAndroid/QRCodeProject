package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.List;

/***
 *   created by zhongrui on 2020/2/13
 */
public class DecodeUtils {
    public static PlanarYUVLuminanceSource startBuildYUV(byte[] data, boolean isVerticalScreen, int screenWidth, int screenHeight, Rect scanRect) {
        PlanarYUVLuminanceSource source;
        if (isVerticalScreen) {
            source = buildLuminanceSource(rotate90(data, screenHeight, screenWidth), screenWidth, screenHeight, scanRect);
        } else {
            source = buildLuminanceSource(data, screenWidth, screenHeight, scanRect);
        }
        return source;
    }

    public static Result startDecode(PlanarYUVLuminanceSource source, List<BarcodeFormat> codeFormatList) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setCodeFormat(codeFormatList);
        Result rawResult = null;

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
        return rawResult;
    }

    public static Bitmap getScanBitmap(PlanarYUVLuminanceSource source) {
        return bundleThumbnail(source);
    }

    private static Bitmap bundleThumbnail(PlanarYUVLuminanceSource source) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        return bitmap;
    }

    private static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height, Rect rect) {
        if (rect == null) {
            rect = new Rect(0, 0, width, height);
        }
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
    }

    public static byte[] rotate90(byte[] originData, int originWidth, int originHeight) {
        if (originData == null) {
            return originData;
        }
        byte[] newData = new byte[originData.length];
        for (int y = 0; y < originHeight; y++) {
            for (int x = 0; x < originWidth; x++) {
                newData[x * originHeight + originHeight - y - 1] = originData[x + y * originWidth];
            }
        }
        return newData;
    }


    public static Result startDecode(Bitmap needDecodeBitmap, BarcodeFormat codeFormat) {
        ArrayList<BarcodeFormat> barcodeFormats = new ArrayList<>();
        barcodeFormats.add(codeFormat);
        return startDecode(needDecodeBitmap, barcodeFormats);
    }

    public static Result startDecode(Bitmap needDecodeBitmap, List<BarcodeFormat> codeFormatList) {
        if (needDecodeBitmap == null) {
            return null;
        }
        int width = needDecodeBitmap.getWidth();
        int height = needDecodeBitmap.getHeight();
        int[] pixels = new int[width * height];
        needDecodeBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        Result rawResult = null;
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setCodeFormat(codeFormatList);
        RGBLuminanceSource source = null;
        try {
            source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            if (source != null) {
                try {
                    rawResult = new MultiFormatReader().decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } finally {
            multiFormatReader.reset();
        }
        return rawResult;
    }
}
