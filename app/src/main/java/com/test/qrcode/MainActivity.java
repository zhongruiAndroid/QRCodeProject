package com.test.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private Button btGoScan;
    private Button btLookScanView;
    private AppCompatCheckBox cbScreenType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        cbScreenType = findViewById(R.id.cbScreenType);
        btGoScan = findViewById(R.id.btGoScan);
        btGoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCamera();
            }
        });

        btLookScanView = findViewById(R.id.btLookScanView);
        btLookScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ScanViewActivity.class));
            }
        });
    }

    private void requestCamera() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100) {
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showMsg("没有相机权限");
        } else {
            Intent intent = new Intent(activity, ScanCodeActivity.class);
            intent.putExtra("type",cbScreenType.isChecked());
            startActivity(intent);
        }
    }
//测试合并111
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
