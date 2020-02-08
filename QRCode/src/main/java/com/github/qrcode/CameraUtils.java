package com.github.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;

import java.util.List;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;

public class CameraUtils {
    /*检查是否有摄像头*/
    public static boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    /*获取相机*/
    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }
    public static Camera getCameraInstanceForBack() {
        int cameraNum=getCameraNum();
        if(cameraNum<=0){
            return null;
        }
        Camera camera = null;
        for (int i = 0; i < cameraNum; i++) {
            Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing==CAMERA_FACING_BACK){
                try {
                    camera = Camera.open(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return camera;
    }
    /*获取摄像头数量*/
    public static int getCameraNum() {
        return Camera.getNumberOfCameras();
    }
    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Context context){
        if(parameters==null){
            throw new IllegalStateException("findBestPreviewSizeValue(Camera.Parameters parameters, Context context) parameters is null");
        }
        if(context==null){
            throw new IllegalStateException("findBestPreviewSizeValue(Camera.Parameters parameters, Context context) context is null");
        }
        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);
        return findBestPreviewSizeValue(parameters,screenWidth,screenHeight);
    }
    public static Point findBestPreviewSizeValue(Camera.Parameters parameters,int screenWidth,int screenHeight){
        if(parameters==null){
            throw new IllegalStateException("findBestPreviewSizeValue(Camera.Parameters parameters, Context context) parameters is null");
        }
        Point point=new Point();
        int tempTotal=screenWidth*screenHeight;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if(supportedPreviewSizes==null){
            throw new IllegalStateException("parameters.getSupportedPreviewSizes() not find supportedPreviewSizes");
        }
        for (Camera.Size size:supportedPreviewSizes){
            int width = size.width;
            int height = size.height;
            if(height==screenWidth&&width==screenHeight){
                point.set(width,height);
                break;
            }
            int count = width * height;
            if(height==screenWidth&&count>tempTotal){
                if(point.x * point.y<count){
                    point.set(width,height);
                }
                continue;
            }
        }
        return point;
    }
    protected static int getScreenWidth(Context context) {
        if (context instanceof Activity&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point outSize = new Point();
            ((Activity)context).getWindowManager().getDefaultDisplay().getRealSize(outSize);
            return outSize.x;
        }else{
            return context.getResources().getDisplayMetrics().widthPixels;
        }
    }

    protected static int getScreenHeight(Context context) {
        Point outSize = new Point();
        if (context instanceof Activity&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((Activity)context).getWindowManager().getDefaultDisplay().getRealSize(outSize);
            return outSize.y;
        }else{
            return context.getResources().getDisplayMetrics().heightPixels;
        }
    }
}
