package com.test.qrcode;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

import com.github.scanview.CodeScanView;

public class ScanViewActivity extends AppCompatActivity  {

    private AppCompatSeekBar sbBorderWidth;
    private AppCompatSeekBar sbCornerWidth;
    private SeekBar sbScanBorderHeight;
    private AppCompatSeekBar sbScanBorderWidth;
    private AppCompatSeekBar sbScanBorderTop;
    private AppCompatSeekBar sbScanBorderLeft;
    private AppCompatSeekBar sbScanLineRightOffset;
    private AppCompatSeekBar sbScanLineLeftOffset;
    private AppCompatSeekBar sbCornerYOffset;
    private AppCompatSeekBar sbScanLineWidth;
    private AppCompatSeekBar sbCornerXOffset;
    private AppCompatSeekBar sbCornerLength;

    private CodeScanView csv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);


        csv = findViewById(R.id.csv);

        sbBorderWidth = findViewById(R.id.sbBorderWidth);
        sbBorderWidth.setOnSeekBarChangeListener(getL());

        sbCornerWidth = findViewById(R.id.sbCornerWidth);
        sbCornerWidth.setOnSeekBarChangeListener(getL());

        sbCornerLength = findViewById(R.id.sbCornerLength);
        sbCornerLength.setOnSeekBarChangeListener(getL());

        sbCornerXOffset = findViewById(R.id.sbCornerXOffset);
        sbCornerXOffset.setOnSeekBarChangeListener(getL());

        sbScanLineWidth = findViewById(R.id.sbScanLineWidth);
        sbScanLineWidth.setOnSeekBarChangeListener(getL());

        sbCornerYOffset = findViewById(R.id.sbCornerYOffset);
        sbCornerYOffset.setOnSeekBarChangeListener(getL());

        sbScanLineLeftOffset = findViewById(R.id.sbScanLineLeftOffset);
        sbScanLineLeftOffset.setOnSeekBarChangeListener(getL());

        sbScanLineRightOffset = findViewById(R.id.sbScanLineRightOffset);
        sbScanLineRightOffset.setOnSeekBarChangeListener(getL());

        sbScanBorderLeft = findViewById(R.id.sbScanBorderLeft);
        sbScanBorderLeft.setOnSeekBarChangeListener(getL());

        sbScanBorderTop = findViewById(R.id.sbScanBorderTop);
        sbScanBorderTop.setOnSeekBarChangeListener(getL());

        sbScanBorderWidth = findViewById(R.id.sbScanBorderWidth);
        sbScanBorderWidth.setOnSeekBarChangeListener(getL());

        sbScanBorderHeight = findViewById(R.id.sbScanBorderHeight);
        sbScanBorderHeight.setOnSeekBarChangeListener(getL());

    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getL() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()){
                    case R.id.sbBorderWidth:
                        csv.setBorderWidth(progress);
                    break;
                    case R.id.sbCornerWidth:
                        csv.setCornerWidth(progress);
                    break;
                    case R.id.sbCornerLength:
                        csv.setCornerLength(progress);
                    break;
                    case R.id.sbCornerXOffset:
                        csv.setCornerXOffset(progress);
                    break;
                    case R.id.sbScanLineWidth:
                        csv.setScanLineWidth(progress);
                    break;
                    case R.id.sbCornerYOffset:
                        csv.setCornerYOffset(progress);
                    break;
                    case R.id.sbScanLineLeftOffset:
                        csv.setScanLineLeftOffset(progress);
                    break;
                    case R.id.sbScanLineRightOffset:
                        csv.setScanLineRightOffset(progress);
                    break;
                    case R.id.sbScanBorderLeft:
                        csv.setScanBorderLeft(progress);
                    break;
                    case R.id.sbScanBorderTop:
                        csv.setScanBorderTop(progress);
                    break;
                    case R.id.sbScanBorderWidth:
                        csv.setScanBorderWidth(progress);
                    break;
                    case R.id.sbScanBorderHeight:
                        csv.setScanBorderHeight(progress);
                    break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
}
