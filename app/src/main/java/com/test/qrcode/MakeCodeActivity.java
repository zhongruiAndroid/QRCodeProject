package com.test.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.qrcode.CreateCodeUtils;
import com.github.qrcode.CreateConfig;
import com.github.selectcolordialog.SelectColorDialog;
import com.github.selectcolordialog.SelectColorListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class MakeCodeActivity extends AppCompatActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView ivCode;
    private RadioGroup rg;
    private RadioGroup rgFormat;
    private SeekBar sbMargin;
    private TextView tvForegroundColor;
    private TextView tvBackgroundColor;
    private CheckBox cbAddIcon;
    private SeekBar sbIconMargin;
    private SeekBar sbIconCorner;
    private SeekBar sbImageCorner;
    private SeekBar sbIconWidth;
    private TextView tvIconForegroundColor;
    private TextView tvIconBackgroundColor;
    private EditText etCodeContent;
    private Button btMake;


    private int margin;
    private int foregroundColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private int iconMargin;
    private int iconCorner;
    private int imageCorner;
    private int iconWidth;
    private int iconForegroundColor = Color.BLACK;
    private int iconBackgroundColor = Color.WHITE;
    private SelectColorDialog selectColorDialog;
    private BarcodeFormat barcodeFormat =BarcodeFormat.QR_CODE;


    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.H;



    private BarcodeFormat[]barcodeFormats={
            BarcodeFormat.AZTEC,
            BarcodeFormat.CODABAR,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.EAN_8,
            BarcodeFormat.EAN_13,
            BarcodeFormat.ITF,
//            BarcodeFormat.MAXICODE,
//            BarcodeFormat.PDF_417,
            BarcodeFormat.QR_CODE,
//            BarcodeFormat.RSS_14,
//            BarcodeFormat.RSS_EXPANDED,
//            BarcodeFormat.UPC_A,
//            BarcodeFormat.UPC_E
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_code);
        initView();
    }

    private void initView() {
        ivCode = findViewById(R.id.ivCode);
        rgFormat = findViewById(R.id.rgFormat);
        rgFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                barcodeFormat=barcodeFormats[checkedId];
            }
        });
        addFormat();

        rg = findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbL:
                        errorCorrectionLevel = ErrorCorrectionLevel.L;
                        break;
                    case R.id.rbM:
                        errorCorrectionLevel = ErrorCorrectionLevel.M;
                        break;
                    case R.id.rbQ:
                        errorCorrectionLevel = ErrorCorrectionLevel.Q;
                        break;
                    case R.id.rbH:
                        errorCorrectionLevel = ErrorCorrectionLevel.H;
                        break;
                }
            }
        });
//        TextView rbL = findViewById(R.id.rbL);
//        TextView rbM=findViewById(R.id.rbM);
//        TextView rbQ=findViewById(R.id.rbQ);
//        TextView rbH=findViewById(R.id.rbH);
        sbMargin = findViewById(R.id.sbMargin);
        sbMargin.setOnSeekBarChangeListener(this);

        tvForegroundColor = findViewById(R.id.tvForegroundColor);
        tvForegroundColor.setOnClickListener(this);

        tvBackgroundColor = findViewById(R.id.tvBackgroundColor);
        tvBackgroundColor.setOnClickListener(this);

        cbAddIcon = findViewById(R.id.cbAddIcon);

        sbIconMargin = findViewById(R.id.sbIconMargin);
        sbIconMargin.setOnSeekBarChangeListener(this);

        sbIconCorner = findViewById(R.id.sbIconCorner);
        sbIconCorner.setOnSeekBarChangeListener(this);

        sbImageCorner = findViewById(R.id.sbImageCorner);
        sbImageCorner.setOnSeekBarChangeListener(this);

        sbIconWidth = findViewById(R.id.sbIconWidth);
        sbIconWidth.setOnSeekBarChangeListener(this);


        tvIconForegroundColor = findViewById(R.id.tvIconForegroundColor);
        tvIconForegroundColor.setOnClickListener(this);

        tvIconBackgroundColor = findViewById(R.id.tvIconBackgroundColor);
        tvIconBackgroundColor.setOnClickListener(this);

        etCodeContent = findViewById(R.id.etCodeContent);

        btMake = findViewById(R.id.btMake);
        btMake.setOnClickListener(this);


        selectColorDialog = new SelectColorDialog(this);
    }

    private void addFormat() {
        rgFormat.removeAllViews();
        int size=barcodeFormats.length;
        for (int i = 0; i < size; i++) {
            RadioButton radioButton=new RadioButton(this);
            radioButton.setText(barcodeFormats[i].toString());
            radioButton.setId(i);
            if(barcodeFormats[i]==BarcodeFormat.QR_CODE){
                radioButton.setChecked(true);
            }
            rgFormat.addView(radioButton);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvForegroundColor:
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        foregroundColor = color;
                        tvForegroundColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
            case R.id.tvBackgroundColor:
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        backgroundColor = color;
                        tvBackgroundColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
            case R.id.tvIconForegroundColor:
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        iconForegroundColor = color;
                        tvIconForegroundColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
            case R.id.tvIconBackgroundColor:
                selectColorDialog.setListener(new SelectColorListener() {
                    @Override
                    public void selectColor(int color) {
                        iconBackgroundColor = color;
                        tvIconBackgroundColor.setBackgroundColor(color);
                    }
                });
                selectColorDialog.show();
                break;
            case R.id.btMake:
                if (TextUtils.isEmpty(etCodeContent.getText())) {
                    Toast.makeText(this, "请输入二维码内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = etCodeContent.getText().toString();



                CreateConfig createConfig = new CreateConfig();
                createConfig.errorCorrection = ErrorCorrectionLevel.H;
                createConfig.setBackgroundColor(backgroundColor);
                createConfig.setForegroundColor(foregroundColor);
                createConfig.setMargin(margin);
                createConfig.setIconBackgroundColor(iconBackgroundColor);
                createConfig.setIconCorner(iconCorner);
                createConfig.setIconImageCorner(imageCorner);
                createConfig.setIconWidth(iconWidth);
                createConfig.setIconMargin(iconMargin);
                createConfig.setCodeFormat(barcodeFormat);
                int size = dp2px(this, 270);
                Bitmap logoBitmap=null;
                if(cbAddIcon.isChecked()){
                      logoBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
                }
                Bitmap bitmap = CreateCodeUtils.createCode(content, logoBitmap,size,size,createConfig);
                if(bitmap!=null){
                    ivCode.setImageBitmap(bitmap);
                }else{
                    Toast.makeText(this, "二维码生成失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private int dp2px(Context context, int value) {
        return (int) (context.getResources().getDisplayMetrics().density * value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sbMargin:
                margin = progress;
                break;
            case R.id.sbIconMargin:
                iconMargin = progress;
                break;
            case R.id.sbIconCorner:
                iconCorner = progress;
                break;
            case R.id.sbImageCorner:
                imageCorner = progress;
                break;
            case R.id.sbIconWidth:
                iconWidth = progress;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
