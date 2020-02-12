package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

/***
 *   created by zhongrui on 2020/2/12
 */
public class CreateCodeUtils {
    public static Bitmap createCode(String content) {
        return createCode(content, null, 800, new CreateConfig());
    }
    public static Bitmap createCode(String content, int size) {
        return createCode(content, null, size, new CreateConfig());
    }
    public static Bitmap createCode(String content, Bitmap logoBitmap) {
        return createCode(content, logoBitmap, 800, new CreateConfig());
    }
    public static Bitmap createCode(String content, Bitmap logoBitmap, int codeSize) {
        return createCode(content, logoBitmap, codeSize, new CreateConfig());
    }
    public static Bitmap createCode(String content, CreateConfig createConfig) {
        return createCode(content, null, 800, createConfig);
    }

    public static Bitmap createCode(String content, int codeSize, CreateConfig createConfig) {
        return createCode(content, null, codeSize, createConfig);
    }

    public static Bitmap createCode(String content, Bitmap logoBitmap, int codeSize, CreateConfig createConfig) {
        return createCode(content,logoBitmap,codeSize,codeSize,createConfig);
    }
    public static Bitmap createCode(String content, Bitmap logoBitmap, int codeWidth, int codeHeight, CreateConfig createConfig) {
        if(content==null){
            content="";
        }
        if (codeWidth <= 0) {
            codeWidth = 200;
        }
        if (codeHeight <= 0) {
            codeHeight = 200;
        }
        if (createConfig == null) {
            createConfig = new CreateConfig();
        }
//        int size = dp2px(this, 270);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
//            createConfig.errorCorrection = errorCorrectionLevel;
//            createConfig.setMargin(margin);
//            createConfig.setIconBackgroundColor(iconBackgroundColor);
//            createConfig.setIconCorner(iconCorner);
//            createConfig.setIconImageCorner(imageCorner);
//            createConfig.setIconWidth(iconWidth);
//            createConfig.setIconMargin(iconMargin);

//            createConfig.setQrVersion(3);

            Map<EncodeHintType, Object> HINTS = new EnumMap<>(EncodeHintType.class);
            HINTS.put(EncodeHintType.CHARACTER_SET, createConfig.getCharacterSet());
            HINTS.put(EncodeHintType.ERROR_CORRECTION, createConfig.errorCorrection);
            HINTS.put(EncodeHintType.MARGIN, createConfig.getMargin());
            HINTS.put(EncodeHintType.QR_VERSION, createConfig.getQrVersion());

            BitMatrix encode = multiFormatWriter.encode(content, createConfig.getCodeFormat(), codeWidth, codeHeight, HINTS);
            int[] pixels = new int[codeWidth * codeHeight];
            for (int y = 0; y < codeHeight; y++) {
                for (int x = 0; x < codeWidth; x++) {
                    if (encode.get(x, y)) {
                        pixels[y * codeWidth + x] = createConfig.getForegroundColor();
                    } else {
                        pixels[y * codeWidth + x] = createConfig.getBackgroundColor();
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(pixels, codeWidth, codeHeight, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);

            if (logoBitmap != null) {

                Canvas canvas = new Canvas(bitmap);

                Matrix matrix = new Matrix();

                //二维码宽度和高度
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                /*不包含iconMargin的图片宽高*/
                int imageW = logoBitmap.getWidth();
//                int imageH = logoBitmap.getHeight();


                /*贴在二维码上面的图片宽高*/
                int iconW = createConfig.getIconWidth();
                if (iconW <= 0) {
                    iconW = imageW;
                }
                imageW = iconW - createConfig.getIconMargin() * 2;
//                imageH = iconW - iconMargin * 2;

                float scale = 1f * imageW / logoBitmap.getWidth();

                int iconH = (int) (logoBitmap.getHeight() * scale + createConfig.getIconMargin() * 2);

                float iconLeft = (width - iconW) / 2;
                float iconTop = (height - iconH) / 2;

                matrix.setTranslate((width - logoBitmap.getWidth()) / 2, (height - logoBitmap.getHeight()) / 2);
                matrix.postScale(scale, scale, width / 2, height / 2);

                if (createConfig.getIconBackgroundColor() != Color.TRANSPARENT) {
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(createConfig.getIconBackgroundColor());
                    paint.setStyle(Paint.Style.FILL);
                    RectF rectF = new RectF(iconLeft, iconTop, iconLeft + iconW, iconTop + iconH);
                    canvas.drawRoundRect(rectF, createConfig.getIconCorner(), createConfig.getIconCorner(), paint);
                }

                //如果小于零,图片圆角自适应icon圆角
                if (createConfig.getIconImageCorner() < 0) {
                    createConfig.setIconImageCorner(createConfig.getIconCorner() * imageW / iconW);
                }
                //如果图片也需要圆角
                int iconImageCorner = createConfig.getIconImageCorner();
                if (iconImageCorner > 0) {
                    int count = canvas.saveLayer(iconLeft, iconTop, iconLeft + iconW, iconTop + iconH, null, Canvas.ALL_SAVE_FLAG);

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//                    paint.setColor(Color.TRANSPARENT);
                    Path roundPath = new Path();
                    RectF rectF = new RectF(0, 0, logoBitmap.getWidth(), logoBitmap.getHeight());
                    matrix.mapRect(rectF);
                    roundPath.addRoundRect(rectF
                            , createConfig.getIconImageCorner(), createConfig.getIconImageCorner()
                            , Path.Direction.CW);

                    canvas.drawPath(roundPath, paint);

                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(logoBitmap, matrix, paint);

                    canvas.restoreToCount(count);
                } else {
                    canvas.drawBitmap(logoBitmap, matrix, null);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}
