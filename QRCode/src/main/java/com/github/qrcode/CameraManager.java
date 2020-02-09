package com.github.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;


public class CameraManager {
    private boolean hasPermission;

    private QRCodeListener listener;
    private Camera camera;
    private ScanConfig scanConfig;
    private LoopThread loopThread;

    private boolean hasSurface;
    private SurfaceHolder surfaceHolder;

    /*是否是竖屏*/
    private boolean isVerticalScreen;

    public CameraManager(QRCodeListener qrCodeListener) {

        if (qrCodeListener == null) {
            throw new IllegalStateException("CameraManager(qrCodeListener) qrCodeListener can not null");
        }

        this.listener = qrCodeListener;

    }

    private QRCodeListener getListener() {
        return listener;
    }

    public boolean hasCamera() {
        return CameraUtils.getCameraNum() > 0;
    }

    public void surfaceCreated(Context context, SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("surfaceCreated(holder) holder can not null");
        }
        hasSurface = true;
        this.surfaceHolder = surfaceHolder;
        onResume(context);
    }

    public void surfaceDestroyed() {
        hasSurface = false;
    }

    public void onResume(Context context) {
        if (context == null) {
            throw new IllegalStateException("onResume(Context context) context can not null");
        }
        /*如果不是横向就判断为竖向*/
        isVerticalScreen=context.getResources().getConfiguration().orientation!=ORIENTATION_LANDSCAPE;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        } else {
            hasPermission = false;
        }


        if (!hasSurface) {
            return;
        }
        /*初始化部分属性*/
        initScanConfig();

        if (camera != null) {
            return;
        }
        boolean flag = CameraUtils.getCameraNum() > 0;
        if (!flag) {
            return;
        }

        if (!hasPermission) {
            return;
        }
        camera = CameraUtils.getCameraInstance();
        if (camera == null) {
            //说明相机已经被打开还没释放
            throw new IllegalStateException("Camera initialization failed because the camera device was already opened(-16),无法启动相机,检查是否有启动相机之后没有释放相机,或者第三方应用后台运行(如手电筒)");
        }
        if(isVerticalScreen){
            camera.setDisplayOrientation(90);
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        Point bestPreviewSizeValue = CameraUtils.findBestPreviewSizeValue(parameters, scanConfig.screenWidth, scanConfig.screenHeight);
        parameters.setPreviewSize(bestPreviewSizeValue.x, bestPreviewSizeValue.y);

        if(isVerticalScreen){
            parameters.setRotation(90);
        }

        camera.setParameters(parameters);


        loopThread = initLoopThread();


        try {
            camera.stopPreview();
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPreviewCallback();
        loopThread.start();
        camera.startPreview();

    }
    public void stopDetect(){
        if (camera == null) {
            return;
        }
        if (loopThread != null) {
            loopThread.stopDetect();
        }
        camera.setPreviewCallback(null);
    }
    public void startDetect(){
        if (loopThread != null) {
            loopThread.startDetect();
        }
        setPreviewCallback();

    }
    private void setPreviewCallback() {
        if (camera == null) {
            return;
        }
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (scanConfig == null) {
                    initScanConfig();
                }
                if (loopThread != null) {
                    loopThread.offer(data);
                }
            }
        });
    }

    private LoopThread initLoopThread() {
        if (loopThread == null) {
            loopThread = new LoopThread(scanConfig.screenWidth, scanConfig.screenHeight, scanConfig.maxFrameNum, scanConfig.scanRect, scanConfig.isNeed);
            loopThread.setQRCodeListener(listener);
            loopThread.setVerticalScreen(isVerticalScreen);
            loopThread.setCodeFormat(scanConfig.codeFormat);
        }
        return loopThread;
    }

    private void quiteLoopThread() {
        if (loopThread != null) {
            loopThread.quit();
        }
    }

    private void initScanConfig() {
        if (scanConfig == null) {
            scanConfig = new ScanConfig(getListener());
        }
        scanConfig.init();
    }

    public void onPause() {
        quiteLoopThread();

        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        if (loopThread != null) {
            loopThread.interrupt();
            loopThread = null;
        }
    }

    public void openLight(boolean isOpen) {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        //打开闪光灯
        parameters.setFlashMode(isOpen ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);//开启
        camera.setParameters(parameters);
    }

    public boolean isOpenLight() {
        if (camera == null) {
            return false;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return false;
        }
        return Camera.Parameters.FLASH_MODE_TORCH.equalsIgnoreCase(parameters.getFlashMode());
    }

    public static void setFullSurfaceView(Activity activity, SurfaceView surfaceView) {
        if (activity == null || surfaceView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
        layoutParams.width = CameraUtils.getScreenWidth(activity);
        layoutParams.height = CameraUtils.getScreenHeight(activity);
        surfaceView.setLayoutParams(layoutParams);
    }
}