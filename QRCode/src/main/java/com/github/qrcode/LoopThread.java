package com.github.qrcode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.Result;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoopThread extends Thread {
    private final int msg_what = 100;
    private final int success_what = 101;
    private Handler handler;
    private Handler mainHandler;
    private Queue<byte[]> frameQueue = new LinkedBlockingQueue(10);
    private ExecutorService executors;
    private QRCodeListener qrCodeListener;
    private AtomicBoolean atomicBoolean;
    private int screenWidth;
    private int screenHeight;
    private Rect scanRect;
    private boolean isNeedGetScanBitmap;
    private List<String> codeFormat;

    public LoopThread(final int screenWidth, final int screenHeight, int maxFrameNum, final Rect scanRect, final boolean isNeedGetScanBitmap) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scanRect = scanRect;
        this.isNeedGetScanBitmap = isNeedGetScanBitmap;

        atomicBoolean = new AtomicBoolean(false);
        executors = Executors.newCachedThreadPool();
        if (maxFrameNum <= 0) {
            maxFrameNum = 10;
        }
        frameQueue = new LinkedBlockingQueue(maxFrameNum);
    }

    public void startEncode(byte[] data, int screenW, int screenH, Rect scanRect, boolean isNeedGetScanBitmap) {
        EncodeRunnable encodeRunnable = new EncodeRunnable(data, screenW, screenH);
        encodeRunnable.setCodeFormat(codeFormat);
        encodeRunnable.setEncodeRect(scanRect);
        encodeRunnable.setNeedGetScanBitmap(isNeedGetScanBitmap);
        encodeRunnable.setListener(new EncodeSuccessListener() {
            @Override
            public void onSuccess(final Result rawResult, final Bitmap bitmap) {
                getResult(rawResult, bitmap);
            }

            @Override
            public void onError() {

            }
        });
        if (executors != null) {
            try {
                executors.execute(encodeRunnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getResult(final Result rawResult, final Bitmap bitmap) {
        if (getHandler() != null && qrCodeListener != null && !atomicBoolean.get()) {
            Message message = Message.obtain();
            if(message==null){
                return;
            }
            message.obj = new Object[]{rawResult, bitmap};
            message.what = success_what;
            getHandler().sendMessage(message);
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
        }
    }

    public void offer(byte[] data) {
        if (frameQueue != null) {
            frameQueue.offer(data);
        }
        if (getHandler() != null) {
            getHandler().sendEmptyMessage(msg_what);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler(new Handler.Callback() {
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
                        if (!atomicBoolean.get()) {
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

                                quit();
                            }
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

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);

            Looper looper = handler.getLooper();
            if (looper == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                looper.quitSafely();
            } else {
                looper.quit();
            }

            handler = null;
        }
        stopAllRunnable();

        atomicBoolean.set(false);
    }

    public void setQRCodeListener(QRCodeListener listener) {
        this.qrCodeListener = listener;
    }
    public void setCodeFormat(List<String> codeFormat) {
        this.codeFormat = codeFormat;
    }
}
