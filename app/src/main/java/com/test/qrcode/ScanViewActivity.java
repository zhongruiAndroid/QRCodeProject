package com.test.qrcode;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.github.qrcode.CodeScanView;
import com.github.selectcolordialog.SelectColorDialog;
import com.github.selectcolordialog.SelectColorListener;

public class ScanViewActivity extends AppCompatActivity implements View.OnClickListener {

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
    private Button btBorderColor;
    private Button btCornerColor;
    private Button btScanLineColor;
    private Button btScanColor;
    private CheckBox cbShowScanColor;
    private CheckBox cbUseDrawable;
    private Button btDrawableColor;
    private SelectColorDialog selectColorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);


        selectColorDialog = new SelectColorDialog(this);

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


        btBorderColor = findViewById(R.id.btBorderColor);
        btBorderColor.setOnClickListener(this);

        btCornerColor = findViewById(R.id.btCornerColor);
        btCornerColor.setOnClickListener(this);

        btScanLineColor = findViewById(R.id.btScanLineColor);
        btScanLineColor.setOnClickListener(this);

        btScanColor = findViewById(R.id.btScanColor);
        btScanColor.setOnClickListener(this);



        cbShowScanColor = findViewById(R.id.cbShowScanColor);
        cbShowScanColor.setChecked(csv.isShowScanColor());
        cbShowScanColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                csv.setShowScanColor(isChecked);
                if(isChecked){
                    cbUseDrawable.setChecked(false);
                    csv.setCenterDrawable(null);
                }
            }
        });

        cbUseDrawable = findViewById(R.id.cbUseDrawable);
        cbUseDrawable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbShowScanColor.setChecked(false);
                    csv.setCenterDrawable(ContextCompat.getDrawable(ScanViewActivity.this,R.drawable.dv9));
                }else{
                    cbShowScanColor.setChecked(true);
                    csv.setCenterDrawable(null);
                    csv.setShowScanColor(true);
                }
            }
        });

        btDrawableColor = findViewById(R.id.btDrawableColor);
        btDrawableColor.setOnClickListener(this);


    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getL() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
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
                        csv.onResume();
                        break;
                    case R.id.sbScanBorderHeight:
                        csv.setScanBorderHeight(progress);
                        csv.onResume();
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

    @Override
    public void onClick(final View v) {
        selectColorDialog.setListener(new SelectColorListener() {
            @Override
            public void selectColor(int color) {
                switch (v.getId()){
                    case R.id.btBorderColor:
                        csv.setBorderColor(color);
                        break;
                    case R.id.btCornerColor:
                        csv.setCornerColor(color);
                        break;
                    case R.id.btScanLineColor:
                        csv.setScanLineColor(color);
                        break;
                    case R.id.btScanColor:
                        csv.setScanColor(color);
                        break;
                    case R.id.btDrawableColor:
                        csv.setCenterDrawableColor(color);
                        break;
                }
            }
        });
        selectColorDialog.show();
    }
}
