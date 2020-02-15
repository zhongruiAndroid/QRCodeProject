package com.github.qrcode;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class CodeScanView extends View {
    private int maskColor;
    private int borderColor;
    private int borderWidth;
    private int cornerWidth;
    private int cornerLength;
    private int cornerColor;
    private int cornerXOffset;
    private int cornerYOffset;

    private int scanLineWidth;
    private int scanLineColor;
    private int scanLineLeftOffset;
    private int scanLineRightOffset;
    private int scanColor = -1;
    private boolean showScanColor;
    private int downTime;
    private Drawable centerDrawable;
    private int centerDrawableColor;
    private Bitmap centerBitmap;

    private int scanBorderLeft;
    private int scanBorderTop;
    private int scanBorderWidth;
    private int scanBorderHeight;


    private Paint borderColorPaint;
    private Paint cornerColorPaint;
    private Paint scanColorPaint;
    private Paint scanLinePaint;

    private LinearGradient linearGradient;
    private Matrix gradientMatrix;

    private ValueAnimator valueAnimator;
    private boolean isStop;


    private float scanBitmapTop;
    private Path borderPath;

    private Path cornerPath1;
    private Path cornerPath2;
    private Path cornerPath3;
    private Path cornerPath4;


    private Paint borderClipPaint;
    private Path borderClipPath;

    private Paint maskPaint;
    private Path maskPath;

    public CodeScanView(Context context) {
        super(context);
        init(context, null);
    }

    public CodeScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CodeScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int dp2px(int value) {
        return (int) (getContext().getResources().getDisplayMetrics().density * value);
    }

    private int defDownTime = 2200;

    private int getDefBorderWidth() {
        return dp2px(1);
    }

    private int getDefCornerWidth() {
        return dp2px(3);
    }

    private int getDefCornerLength() {
        return dp2px(20);
    }

    private int getDefScanLineWidth() {
        return 3;
    }

    private int getDefColor() {
        return Color.parseColor("#108EE9");
    }

    private void initPaint() {
        borderColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderColorPaint.setColor(borderColor);
        borderColorPaint.setStrokeWidth(borderWidth * 2);
        borderColorPaint.setStyle(Paint.Style.STROKE);

        borderClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderClipPaint.setStyle(Paint.Style.STROKE);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setStyle(Paint.Style.FILL);
        maskPaint.setColor(maskColor);

        cornerColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cornerColorPaint.setColor(cornerColor);
        cornerColorPaint.setStyle(Paint.Style.FILL);

        scanLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scanLinePaint.setColor(scanLineColor);
        scanLinePaint.setStrokeWidth(scanLineWidth);

        scanColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    }

    private void initPath() {
        borderPath = new Path();
        cornerPath1 = new Path();
        cornerPath2 = new Path();
        cornerPath3 = new Path();
        cornerPath4 = new Path();

        borderClipPath = new Path();
        maskPath = new Path();

    }

    private void init(Context context, AttributeSet attrs) {


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CodeScanView);

        maskColor = typedArray.getColor(R.styleable.CodeScanView_maskColor, Color.TRANSPARENT);
        borderColor = typedArray.getColor(R.styleable.CodeScanView_borderColor, getDefColor());
        borderWidth = (int) typedArray.getDimension(R.styleable.CodeScanView_borderWidth, getDefBorderWidth());
        cornerWidth = (int) typedArray.getDimension(R.styleable.CodeScanView_cornerWidth, getDefCornerWidth());
        cornerLength = (int) typedArray.getDimension(R.styleable.CodeScanView_cornerLength, getDefCornerLength());
        cornerColor = typedArray.getColor(R.styleable.CodeScanView_cornerColor, getDefColor());
        cornerXOffset = (int) typedArray.getDimension(R.styleable.CodeScanView_cornerXOffset, 0);
        cornerYOffset = (int) typedArray.getDimension(R.styleable.CodeScanView_cornerYOffset, 0);

        scanLineWidth = (int) typedArray.getDimension(R.styleable.CodeScanView_scanLineWidth, getDefScanLineWidth());
        scanLineColor = typedArray.getColor(R.styleable.CodeScanView_scanLineColor, getDefColor());

        scanLineLeftOffset = (int) typedArray.getDimension(R.styleable.CodeScanView_scanLineLeftOffset, 0);
        scanLineRightOffset = (int) typedArray.getDimension(R.styleable.CodeScanView_scanLineRightOffset, 0);
        scanColor = typedArray.getColor(R.styleable.CodeScanView_scanColor, Color.parseColor("#60108EE9"));

        showScanColor = typedArray.getBoolean(R.styleable.CodeScanView_showScanColor, true);


        downTime = typedArray.getInt(R.styleable.CodeScanView_downTime, defDownTime);

        centerDrawable = typedArray.getDrawable(R.styleable.CodeScanView_centerDrawable);
        centerDrawableColor = typedArray.getColor(R.styleable.CodeScanView_centerDrawableColor, -1);

        updateScanDrawable();
        scanBorderLeft = (int) typedArray.getDimension(R.styleable.CodeScanView_scanBorderLeft, -1);
        scanBorderTop = (int) typedArray.getDimension(R.styleable.CodeScanView_scanBorderTop, -1);
        scanBorderWidth = (int) typedArray.getDimension(R.styleable.CodeScanView_scanBorderWidth, -1);
        scanBorderHeight = (int) typedArray.getDimension(R.styleable.CodeScanView_scanBorderHeight, -1);

        typedArray.recycle();


        initPaint();
        initPath();
        startAnim();
    }

    private void updateScanDrawable() {
        if (centerDrawable != null) {
            if (centerDrawableColor != -1) {
                Drawable wrappedDrawable = DrawableCompat.wrap(centerDrawable);
                DrawableCompat.setTint(wrappedDrawable, centerDrawableColor);
                centerBitmap = drawable2Bitmap(wrappedDrawable);
            } else {
                centerBitmap = drawable2Bitmap(centerDrawable);
            }
        }
    }

    private Bitmap drawable2Bitmap(Drawable centerDrawable) {
        if (centerDrawable instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) centerDrawable;
            return drawable.getBitmap();
        }
        int w = centerDrawable.getIntrinsicWidth();
        int h = centerDrawable.getIntrinsicHeight();
        Bitmap.Config config = centerDrawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        centerDrawable.setBounds(0, 0, w, h);
        centerDrawable.draw(canvas);
        return bitmap;
    }


    public int getScanBorderLeft() {
        return scanBorderLeft;
    }

    public int getScanBorderTop() {
        return scanBorderTop;
    }

    public int getScanBorderRight() {
        return getScanBorderLeft() + scanBorderWidth;
    }

    public int getScanBorderBottom() {
        return getScanBorderTop() + scanBorderHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        /*扫描框坐标和宽高*/
        updateScanXY(w, h);
        /*扫描框path*/
        updateBorderPath();
        /*扫描框的四个角*/
        updateCornerPath();

        if (isInEditMode()) {
            scanBitmapTop = getScanBorderHeight() / 2 + getScanBorderTop();
        }

    }

    private void updateLinearGradient() {
        linearGradient = new LinearGradient(0, scanBitmapTop - scanBorderHeight, 0, scanBitmapTop, new int[]{Color.TRANSPARENT, scanColor}, new float[]{0f, 1}, Shader.TileMode.CLAMP);
        scanColorPaint.setShader(linearGradient);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(maskColor!=Color.TRANSPARENT){
            canvas.drawPath(maskPath,maskPaint);
        }
        int count = canvas.saveLayer(getScanBorderLeft(), getScanBorderTop(), getScanBorderRight(), getScanBorderBottom(), null, Canvas.ALL_SAVE_FLAG);

        if (showScanColor && centerBitmap == null) {
            canvas.drawRect(new RectF(scanBorderLeft + borderWidth, scanBitmapTop - scanBorderHeight + scanLineWidth / 2, scanBorderWidth + scanBorderLeft - borderWidth, scanBitmapTop + scanLineWidth / 2), scanColorPaint);
        }
        if (centerBitmap == null) {
            canvas.drawLine(scanBorderLeft + borderWidth + scanLineLeftOffset, scanBitmapTop, scanBorderLeft + scanBorderWidth - scanLineRightOffset - borderWidth, scanBitmapTop, scanLinePaint);
        } else {
            int width = centerBitmap.getWidth();
            int offset = (scanBorderWidth - width) / 2;
            canvas.drawBitmap(centerBitmap, scanBorderLeft + offset, scanBitmapTop, null);
        }

        if (getBorderWidth() > 0) {
            canvas.drawPath(borderPath, borderColorPaint);
        }

        borderClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawPath(borderClipPath, borderClipPaint);

        if (isInEditMode()) {
            canvas.restore();
        } else {
            canvas.restoreToCount(count);
        }

        canvas.drawPath(cornerPath1, cornerColorPaint);
        canvas.drawPath(cornerPath2, cornerColorPaint);
        canvas.drawPath(cornerPath3, cornerColorPaint);
        canvas.drawPath(cornerPath4, cornerColorPaint);


    }

    private void updateScanXY(int w, int h) {

        int tempMin = Math.min(w, h);
        if (scanBorderWidth <= 0 && scanBorderHeight <= 0) {
            scanBorderWidth = tempMin * 3 / 5;
            scanBorderHeight = tempMin * 3 / 5;
        } else if (scanBorderWidth > 0 && scanBorderHeight > 0) {

        } else {
            scanBorderWidth = Math.max(scanBorderWidth, scanBorderHeight);
            scanBorderHeight = scanBorderWidth;
        }
        if (scanBorderLeft < 0) {
            scanBorderLeft = (w - scanBorderWidth) / 2;
        }
        if (scanBorderTop < 0) {
            scanBorderTop = (h - scanBorderHeight) / 2;
        }


        updateLinearGradient();

    }


    private void updateBorderPath() {
        if (borderPath == null) {
            borderPath = new Path();
        } else {
            borderPath.reset();
        }

        borderPath.addRect(new RectF(scanBorderLeft, scanBorderTop, scanBorderLeft + scanBorderWidth, scanBorderTop + scanBorderHeight), Path.Direction.CW);
        updateClipPath();
    }

    private void updateClipPath() {
        if (borderClipPath == null) {
            borderClipPath = new Path();
        } else {
            borderClipPath.reset();
        }
        borderClipPath.addRect(new RectF(scanBorderLeft + borderWidth, scanBorderTop + borderWidth, scanBorderLeft + scanBorderWidth - borderWidth, scanBorderTop + scanBorderHeight - borderWidth), Path.Direction.CW);

        if (maskPath == null) {
            maskPath = new Path();
        } else {
            maskPath.reset();
        }
        /*左边*/
        maskPath.addRect(0, 0, scanBorderLeft + borderWidth, getHeight(), Path.Direction.CW);
        /*右边*/
        maskPath.addRect(scanBorderLeft + scanBorderWidth - borderWidth, 0, getWidth(), getHeight(), Path.Direction.CW);
        /*上边*/
        maskPath.addRect(scanBorderLeft + borderWidth, 0, scanBorderLeft + scanBorderWidth - borderWidth, scanBorderTop + borderWidth, Path.Direction.CW);
        /*下边*/
        maskPath.addRect(scanBorderLeft + borderWidth,  scanBorderTop + scanBorderHeight - borderWidth, scanBorderLeft + scanBorderWidth - borderWidth, getHeight(), Path.Direction.CW);

    }

    private void updateCornerPath() {
        int x = scanBorderLeft + cornerXOffset;
        int y = scanBorderTop + cornerYOffset;
        if (cornerPath1 == null) {
            cornerPath1 = new Path();
        } else {
            cornerPath1.reset();
        }

        cornerPath1.moveTo(x, y);
        cornerPath1.lineTo(x, y + cornerLength);
        cornerPath1.rLineTo(-cornerWidth, 0);
        cornerPath1.rLineTo(0, -cornerLength - cornerWidth);
        cornerPath1.rLineTo(cornerLength + cornerWidth, 0);
        cornerPath1.rLineTo(0, cornerWidth);
        cornerPath1.close();


        x = scanBorderLeft + scanBorderWidth - cornerXOffset;
        y = scanBorderTop + cornerYOffset;
        if (cornerPath2 == null) {
            cornerPath2 = new Path();
        } else {
            cornerPath2.reset();
        }

        cornerPath2.moveTo(x, y);
        cornerPath2.lineTo(x, y + cornerLength);
        cornerPath2.rLineTo(cornerWidth, 0);
        cornerPath2.rLineTo(0, -cornerLength - cornerWidth);
        cornerPath2.rLineTo(-cornerLength - cornerWidth, 0);
        cornerPath2.rLineTo(0, cornerWidth);
        cornerPath2.close();


        x = scanBorderLeft + scanBorderWidth - cornerXOffset;
        y = scanBorderTop + scanBorderHeight - cornerYOffset;

        if (cornerPath3 == null) {
            cornerPath3 = new Path();
        } else {
            cornerPath3.reset();
        }

        cornerPath3.moveTo(x, y);
        cornerPath3.lineTo(x - cornerLength, y);
        cornerPath3.rLineTo(0, cornerWidth);
        cornerPath3.rLineTo(cornerLength + cornerWidth, 0);
        cornerPath3.rLineTo(0, -cornerLength - cornerWidth);
        cornerPath3.rLineTo(-cornerWidth, 0);
        cornerPath3.close();


        x = scanBorderLeft + cornerXOffset;
        y = scanBorderTop + scanBorderHeight - cornerYOffset;

        if (cornerPath4 == null) {
            cornerPath4 = new Path();
        } else {
            cornerPath4.reset();
        }

        cornerPath4.moveTo(x, y);
        cornerPath4.lineTo(x + cornerLength, y);
        cornerPath4.rLineTo(0, cornerWidth);
        cornerPath4.rLineTo(-cornerLength - cornerWidth, 0);
        cornerPath4.rLineTo(0, -cornerLength - cornerWidth);
        cornerPath4.rLineTo(cornerWidth, 0);
        cornerPath4.close();

    }

    public void onPause() {
        isStop = true;
        stopAnim();
    }

    public void onResume() {
        isStop = false;
        startAnim();
    }

    private void startAnim() {
        post(new Runnable() {
            @Override
            public void run() {
                if (gradientMatrix == null) {
                    gradientMatrix = new Matrix();
                }
                updateBorderPath();

                if (valueAnimator != null && valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
                valueAnimator = ValueAnimator.ofFloat(borderWidth, scanBorderHeight - borderWidth);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float animatedValue = (float) animation.getAnimatedValue();
                        scanBitmapTop = animatedValue + getScanBorderTop();
                        gradientMatrix.setTranslate(0, animatedValue);
                        if (linearGradient != null) {
                            linearGradient.setLocalMatrix(gradientMatrix);
                        }
                        if (!isStop) {
                            invalidate();
                        }
                    }
                });
                valueAnimator.setDuration(downTime);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.start();
            }
        });

    }

    private void stopAnim() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isStop = true;
        stopAnim();
    }

    public void setScanBorder(int left, int top, int scanWidth, int scanHeight) {
        if (left < 0
                || top < 0
                || scanWidth <= 0
                || scanHeight <= 0) {
            return;

        }

        scanBorderLeft = left;
        scanBorderTop = top;

        scanBorderWidth = scanWidth;
        scanBorderHeight = scanHeight;
        if (scanColor == -1) {
            scanColor = Color.parseColor("#60108EE9");
        }

        updateLinearGradient();

        updateBorderPath();
        updateCornerPath();
    }


    public Rect getScanRectForView() {
        int left = getScanBorderLeft();
        int top = getScanBorderTop();
        int right = getScanBorderRight();
        int bottom = getScanBorderBottom();
        Rect windowRect = new Rect(left, top, right, bottom);
        return windowRect;
    }

    @Deprecated
    public RectF getScanRectFForView() {
        int left = getScanBorderLeft();
        int top = getScanBorderTop();
        int right = getScanBorderRight();
        int bottom = getScanBorderBottom();
        RectF windowRect = new RectF(left, top, right, bottom);
        return windowRect;
    }
   /* private Point parentLeftAndTop(View view, int left, int top) {
        Point point = new Point();
        if (view == null) {
            point.set(0, 0);
            return point;
        }
        point.set(point.x + left, point.y + top);
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            int viewLeft = viewGroup.getLeft();
            int viewTop = viewGroup.getTop();
            point.set(point.x + viewLeft, point.y + viewTop);
            parent = viewGroup.getParent();
        }
        return point;
    }

    private int getStatusBarHeight() {
        try {
            Resources resources = getContext().getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        } catch (Exception e) {
        }
        return 0;
    }*/


/*
    private int getScreenWidth(Context context) {
        if (context instanceof Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point outSize = new Point();
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(outSize);
            return outSize.x;
        } else {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
    }

    private int getScreenHeight(Context context) {
        Point outSize = new Point();
        if (context instanceof Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(outSize);
            return outSize.y;
        } else {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
    }*/

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        if (this.maskColor == maskColor) {
            return;
        }
        this.maskColor = maskColor;
        maskPaint.setColor(maskColor);
        invalidate();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        if (this.borderColor == borderColor) {
            return;
        }
        this.borderColor = borderColor;

        borderColorPaint.setColor(this.borderColor);
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (this.borderWidth == borderWidth) {
            return;
        }
        this.borderWidth = borderWidth;
        borderColorPaint.setStrokeWidth(this.borderWidth * 2);
        updateClipPath();
    }

    public int getCornerWidth() {
        return cornerWidth;
    }

    public void setCornerWidth(int cornerWidth) {
        if (this.cornerWidth == cornerWidth) {
            return;
        }
        this.cornerWidth = cornerWidth;

        updateCornerPath();
    }

    public int getCornerLength() {
        return cornerLength;
    }

    public void setCornerLength(int cornerLength) {
        if (this.cornerLength == cornerLength) {
            return;
        }
        this.cornerLength = cornerLength;
        updateCornerPath();
    }

    public int getCornerColor() {
        return cornerColor;
    }

    public void setCornerColor(int cornerColor) {
        if (this.cornerColor == cornerColor) {
            return;
        }
        this.cornerColor = cornerColor;

        cornerColorPaint.setColor(cornerColor);
    }

    public int getCornerXOffset() {
        return cornerXOffset;
    }

    public void setCornerXOffset(int cornerXOffset) {
        if (this.cornerXOffset == cornerXOffset) {
            return;
        }
        this.cornerXOffset = cornerXOffset;
        updateCornerPath();
    }

    public int getCornerYOffset() {
        return cornerYOffset;
    }

    public void setCornerYOffset(int cornerYOffset) {
        if (this.cornerYOffset == cornerYOffset) {
            return;
        }
        this.cornerYOffset = cornerYOffset;
        updateCornerPath();
    }

    public int getScanLineWidth() {
        return scanLineWidth;
    }

    public void setScanLineWidth(int scanLineWidth) {
        if (this.scanLineWidth == scanLineWidth) {
            return;
        }
        this.scanLineWidth = scanLineWidth;

        scanLinePaint.setStrokeWidth(scanLineWidth);
    }


    public int getScanLineColor() {
        return scanLineColor;
    }

    public void setScanLineColor(int scanLineColor) {
        if (this.scanLineColor == scanLineColor) {
            return;
        }
        this.scanLineColor = scanLineColor;
        scanLinePaint.setColor(scanLineColor);
    }

    public int getScanLineLeftOffset() {
        return scanLineLeftOffset;
    }

    public void setScanLineLeftOffset(int scanLineLeftOffset) {
        if (this.scanLineLeftOffset == scanLineLeftOffset) {
            return;
        }
        this.scanLineLeftOffset = scanLineLeftOffset;
    }

    public int getScanLineRightOffset() {
        return scanLineRightOffset;
    }

    public void setScanLineRightOffset(int scanLineRightOffset) {
        if (this.scanLineRightOffset == scanLineRightOffset) {
            return;
        }
        this.scanLineRightOffset = scanLineRightOffset;
    }

    public int getScanColor() {
        return scanColor;
    }

    public void setScanColor(int scanColor) {
        if (this.scanColor == scanColor) {
            return;
        }
        this.scanColor = scanColor;
        updateLinearGradient();
    }

    public boolean isShowScanColor() {
        return showScanColor;
    }

    public void setShowScanColor(boolean showScanColor) {
        if (this.showScanColor == showScanColor) {
            return;
        }
        this.showScanColor = showScanColor;
    }

    public int getDownTime() {
        return downTime;
    }

    public void setDownTime(int downTime) {
        if (this.downTime == downTime) {
            return;
        }
        this.downTime = downTime;
    }

    public Drawable getCenterDrawable() {
        return centerDrawable;
    }

    public void setCenterDrawable(Drawable centerDrawable) {
        if (this.centerDrawable == centerDrawable) {
            return;
        }
        this.centerDrawable = centerDrawable;
        if (this.centerDrawable == null) {
            this.centerBitmap = null;
        } else {
            setShowScanColor(false);
        }
        updateScanDrawable();
    }

    public int getCenterDrawableColor() {
        return centerDrawableColor;
    }

    public void setCenterDrawableColor(int centerDrawableColor) {
        if (this.centerDrawableColor == centerDrawableColor) {
            return;
        }
        this.centerDrawableColor = centerDrawableColor;
        updateScanDrawable();
    }

    public void setScanBorderLeft(int scanBorderLeft) {
        if (this.scanBorderLeft == scanBorderLeft) {
            return;
        }
        this.scanBorderLeft = scanBorderLeft;
        if (getWidth() <= 0) {
            return;
        }
        updateScanXY(getWidth(), getHeight());
        updateBorderPath();
        updateCornerPath();
    }

    public void setScanBorderTop(int scanBorderTop) {
        if (this.scanBorderTop == scanBorderTop) {
            return;
        }
        this.scanBorderTop = scanBorderTop;
        if (getWidth() <= 0) {
            return;
        }
        updateScanXY(getWidth(), getHeight());
        updateBorderPath();
        updateCornerPath();
    }

    public int getScanBorderWidth() {
        return scanBorderWidth;
    }

    public void setScanBorderWidth(int scanBorderWidth) {
        if (this.scanBorderWidth == scanBorderWidth) {
            return;
        }
        this.scanBorderWidth = scanBorderWidth;
        if (getWidth() <= 0) {
            return;
        }
        /*扫描框坐标和宽高*/
        updateScanXY(getWidth(), getHeight());
        /*扫描框path*/
        updateBorderPath();
        /*扫描框的四个角*/
        updateCornerPath();
    }

    public int getScanBorderHeight() {
        return scanBorderHeight;
    }

    public void setScanBorderHeight(int scanBorderHeight) {
        if (this.scanBorderHeight == scanBorderHeight) {
            return;
        }
        this.scanBorderHeight = scanBorderHeight;

        if (getWidth() <= 0) {
            return;
        }
        /*扫描框坐标和宽高*/
        updateScanXY(getWidth(), getHeight());
        /*扫描框path*/
        updateBorderPath();
        /*扫描框的四个角*/
        updateCornerPath();
    }

}
