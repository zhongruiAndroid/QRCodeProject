package com.test.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private Button btGoScan;
    private Button btLookScanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        setContentView(R.layout.activity_main);
        btGoScan = findViewById(R.id.btGoScan);
        btGoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity,ScanCodeActivity.class));
            }
        });

        btLookScanView = findViewById(R.id.btLookScanView);
        btLookScanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity,ScanViewActivity.class));
            }
        });
    }
}
