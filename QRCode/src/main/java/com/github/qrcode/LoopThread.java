package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.qrcode.ScanConfig.MAX_SIZE;

public class LoopThread extends Thread {
    private final int msg_what = 100;
    private final int success_what = 101;
    private Handler threadHandler;
    private Handler mainHandler;
    private Queue<byte[]> frameQueue = new LinkedBlockingQueue(MAX_SIZE);
    private AtomicInteger frameNum;
    private ExecutorService executors;
    private QRCodeListener qrCodeListener;
    private AtomicBoolean atomicBoolean;
    private int screenWidth;
    private int screenHeight;
    private Rect scanRect;
    private boolean isNeedGetScanBitmap;
    private List<BarcodeFormat> codeFormat;
    private int maxSize=MAX_SIZE;

    public LoopThread(final int screenWidth, final int screenHeight, int maxFrameNum, final Rect scanRect, final boolean isNeedGetScanBitmap) {
        frameNum=new AtomicInteger();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scanRect = scanRect;
        this.isNeedGetScanBitmap = isNeedGetScanBitmap;

        atomicBoolean = new AtomicBoolean(false);
        executors = Executors.newCachedThreadPool();
        if (maxFrameNum <= 0) {
            maxFrameNum = MAX_SIZE;
        }
        maxSize=maxFrameNum;
        frameQueue = new LinkedBlockingQueue(maxFrameNum);
    }
    private boolean isVerticalScreen;
    public void setVerticalScreen(boolean isVerticalScreen){
        this.isVerticalScreen=isVerticalScreen;
    }
    public void startEncode(byte[] data, int screenW, int screenH, Rect scanRect, boolean isNeedGetScanBitmap) {
        DecodeRunnable decodeRunnable = new DecodeRunnable(data, screenW, screenH);
        decodeRunnable.setCodeFormat(codeFormat);
        decodeRunnable.setVerticalScreen(isVerticalScreen);
        decodeRunnable.setEncodeRect(scanRect);
        decodeRunnable.setNeedGetScanBitmap(isNeedGetScanBitmap);
        decodeRunnable.setListener(new DecodeSuccessListener() {
            @Override
            public void onSuccess(final Result rawResult, final Bitmap bitmap) {
                reduceFrame();
                getResult(rawResult, bitmap);
            }

            @Override
            public void onError() {
                reduceFrame();
            }
        });
        if (executors != null) {
            try {
                executors.execute(decodeRunnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void addFrame(){
        if(frameNum!=null){
            frameNum.incrementAndGet();
        }
    }
    private void reduceFrame(){
        if(frameNum!=null){
            frameNum.decrementAndGet();
        }
    }
    public int getFrameNum(){
        return frameNum==null?0:frameNum.get();
    }
    public void startDetect(){
        atomicBoolean = new AtomicBoolean(false);
    }
    public void stopDetect(){
        atomicBoolean = new AtomicBoolean(true);
    }
    private void getResult(final Result rawResult, final Bitmap bitmap) {
        if (getThreadHandler() != null && qrCodeListener != null && !atomicBoolean.get()) {
            Message message = Message.obtain();
            if(message==null){
                return;
            }
            message.obj = new Object[]{rawResult, bitmap};
            message.what = success_what;
            getThreadHandler().sendMessage(message);
        }
    }

    private void stopAllRunnable() {
        if (executors == null) {
            return;
        }
        try {
            executors.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executors = null;
        }
    }

    public void clearFrame() {
        if (frameQueue != null) {
            frameQueue.clear();
            frameNum.set(0);
        }
    }

    public void offer(byte[] data) {
        if(atomicBoolean==null||atomicBoolean.get()){
            return;
        }
        if (data!=null&&frameQueue != null&&getFrameNum()<maxSize) {
            frameQueue.offer(data);
            addFrame();
        }
        if (getThreadHandler() != null) {
            getThreadHandler().sendEmptyMessage(msg_what);
        }
    }

    public Handler getThreadHandler() {
        return threadHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        threadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case msg_what:
                        if (frameQueue == null) {
                            return false;
                        }
                        if (msg.what != msg_what) {
                            return false;
                        }

                        byte[] poll = frameQueue.poll();
                        if (poll == null) {
                            return false;
                        }
                        startEncode(poll, screenWidth, screenHeight, scanRect, isNeedGetScanBitmap);
                        break;
                    case success_what:
                        if (atomicBoolean.get()) {
                            return false;
                        }
                        Object msgObj = msg.obj;
                        if (msgObj == null || !(msgObj instanceof Object[])) {
                            break;
                        }
                        Object[] objects = (Object[]) msgObj;
                        if (objects.length == 2) {
                            final Result rawResult = (Result) objects[0];
                            final Bitmap bitmap = (Bitmap) objects[1];
                            atomicBoolean.set(true);

                            if (qrCodeListener != null) {
                                getMainHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        qrCodeListener.onSuccess(rawResult, bitmap);
                                    }
                                });
                            }
//                                quit();
                            clearFrame();
                        }
                        break;
                }
                return false;
            }
        });
        Looper.loop();
    }
    private Handler getMainHandler(){
        if(mainHandler==null){
            mainHandler=new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }
    public void quit() {
        clearFrame();

        if (threadHandler != null) {
            threadHandler.removeCallbacksAndMessages(null);

            Looper looper = threadHandler.getLooper();
            if (looper == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                looper.quitSafely();
            } else {
                looper.quit();
            }

            threadHandler = null;
        }
        stopAllRunnable();

        atomicBoolean.set(false);
    }

    public void setQRCodeListener(QRCodeListener listener) {
        this.qrCodeListener = listener;
    }
    public void setCodeFormat(List<BarcodeFormat> codeFormat) {
        this.codeFormat = codeFormat;
    }

}
