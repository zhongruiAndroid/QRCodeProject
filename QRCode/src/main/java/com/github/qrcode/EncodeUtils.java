package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

/***
 *   created by zhongrui on 2020/2/13
 */
public class EncodeUtils {
    public static Result startEncode(byte[] data, boolean isVerticalScreen, int screenWidth, int screenHeight, Rect scanRect) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setCodeFormat(null);
        Result rawResult = null;

        PlanarYUVLuminanceSource source;
        if (isVerticalScreen) {
            source = buildLuminanceSource(rotate90(data, screenHeight, screenWidth), screenWidth, screenHeight, scanRect);
        } else {
            source = buildLuminanceSource(data, screenWidth, screenHeight, scanRect);
        }
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
}
