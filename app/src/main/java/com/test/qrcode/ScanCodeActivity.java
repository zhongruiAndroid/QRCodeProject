package com.test.qrcode;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.qrcode.CameraManager;
import com.github.qrcode.CodeScanView;
import com.github.qrcode.QRCodeListener;
import com.google.zxing.Result;

import java.util.List;

public class ScanCodeActivity extends AppCompatActivity implements QRCodeListener {

    private SurfaceView svPreview;
    private CodeScanView csv;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;

    private Button btOpenLight;


    boolean isOpen;
    private SurfaceHolder.Callback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

        svPreview = findViewById(R.id.svPreview);
        csv = findViewById(R.id.csv);
        btOpenLight = findViewById(R.id.btOpenLight);


        /*闪光灯*/
        btOpenLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    cameraManager.openLight(false);
                    btOpenLight.setText("打开闪光灯");
                } else {
                    cameraManager.openLight(true);
                    btOpenLight.setText("关闭闪光灯");
                }
                isOpen = !isOpen;
            }
        });



        /*实例化CameraManager*/
        cameraManager = new CameraManager(this);
        /*给surfaceView的宽高设置为手机分辨率大小*/
        CameraManager.setFullSurfaceView(this, svPreview);

        /*常规操作*/
        surfaceHolder = svPreview.getHolder();
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cameraManager.surfaceCreated(ScanCodeActivity.this, surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(final SurfaceHolder holder) {
                cameraManager.surfaceDestroyed();
            }
        };
        surfaceHolder.addCallback(callback);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.onPause();
    }

    @Override
    public Activity getAct() {
        return this;
    }

    @Override
    public Rect getScanRect() {
        return csv.getScanRectForWindow();
    }
    @Override
    public boolean needGetBitmapForSuccess() {
        return true;
    }
    @Override
    public int getMaxFrameNum() {
        return 6;
    }

    private boolean jumpAct = false;

    @Override
    public void onSuccess(Result rawResult, Bitmap bitmap) {
        if (jumpAct) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("content", rawResult.getText());
            startActivity(intent);
            return;
        }
        /*暂停检测，此时没有暂停相机(页面跳转就暂停相机)*/
        cameraManager.stopDetect();
        /*暂停动画*/
        csv.onPause();

        final Dialog dialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        dialog.setContentView(view);

        TextView tvContent = view.findViewById(R.id.tvContent);
        ImageView iv = view.findViewById(R.id.iv);
        Button btAgain = view.findViewById(R.id.btAgain);
        btAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        String text = rawResult.getText();
        if (bitmap == null) {
            tvContent.setText("(无扫描图片)\n扫描文字结果:" + text);
        } else {
            tvContent.setText("(有扫描图片)\n扫描文字结果:" + text);
        }
        iv.setImageBitmap(bitmap);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                /*继续检测*/
                cameraManager.startDetect();
                /*继续动画*/
                csv.onResume();
            }
        });
        dialog.show();
    }

    @Override
    public List<String> getCodeFormat() {
        /*返回null默认为CodeFormat.QR_CODE*/
        return null;
    }
}
