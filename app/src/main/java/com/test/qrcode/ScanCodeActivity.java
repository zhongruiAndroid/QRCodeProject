package com.test.qrcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import com.github.qrcode.CodeScanView;

public class ScanCodeActivity extends AppCompatActivity implements QRCodeListener {

    private SurfaceView svPreview;
    private CodeScanView csv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

        svPreview = findViewById(R.id.svPreview);

        csv = findViewById(R.id.csv);
    }
}
