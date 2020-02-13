package com.test.qrcode;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.github.qrcode.CreateConfig;
import com.github.qrcode.DecodeUtils;
import com.github.qrcode.EncodeUtils;
import com.github.selectcolordialog.SelectColorDialog;
import com.github.selectcolordialog.SelectColorListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.List;

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
    private Button btDecode;


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
    private BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;


    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.H;


    private BarcodeFormat[] barcodeFormats = {
            BarcodeFormat.QR_CODE,
            BarcodeFormat.AZTEC,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.EAN_8,
            BarcodeFormat.EAN_13,
//            BarcodeFormat.ITF,
            BarcodeFormat.PDF_417,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.CODABAR
    };
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_code);
        initView();
    }

    private void initView() {
        ivCode = findViewById(R.id.ivCode);


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

        btDecode = findViewById(R.id.btDecode);
        btDecode.setOnClickListener(this);


        selectColorDialog = new SelectColorDialog(this);


        rgFormat = findViewById(R.id.rgFormat);
        rgFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                barcodeFormat = barcodeFormats[checkedId];
                etCodeContent.setEnabled(false);
                switch (barcodeFormat) {
                    case AZTEC:
                        etCodeContent.setEnabled(true);
                        etCodeContent.setText("AZTEC_123456");
                        break;
                    case CODE_39:
                        etCodeContent.setText("CODE39");
                        break;
                    case CODE_93:
                        etCodeContent.setText("CODE_93");
                        break;
                    case CODE_128:
                        etCodeContent.setText("CODE_128");
                        break;
                    case DATA_MATRIX:
                        etCodeContent.setText("DATA_MATRIX");
                        break;
                    case EAN_8:
                        etCodeContent.setText("1234567");
                        break;
                    case EAN_13:
                        etCodeContent.setText("123456789101");
                        break;
                    case ITF:
                        etCodeContent.setText("12");
                        break;
                    case PDF_417:
                        etCodeContent.setEnabled(true);
                        etCodeContent.setText("PDF_417_可以有中文");
                        break;
                    case QR_CODE:
                        etCodeContent.setEnabled(true);
                        etCodeContent.setText("QR_CODE_可以有中文");
                        break;
                    case UPC_A:
                        etCodeContent.setText("123456789012");
                        break;
                    case UPC_E:
                        etCodeContent.setText("1234567");
                        break;
                    case CODABAR:
                        etCodeContent.setText("1234567");
                        break;

                }
            }
        });
        addFormat();
    }

    private void addFormat() {
        rgFormat.removeAllViews();
        int size = barcodeFormats.length;
        for (int i = 0; i < size; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(barcodeFormats[i].toString());
            radioButton.setId(i);
            if (barcodeFormats[i] == BarcodeFormat.QR_CODE) {
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
                Bitmap logoBitmap = null;
                if (cbAddIcon.isChecked()) {
                    logoBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
                }
                bitmap = EncodeUtils.createCode(content, logoBitmap, size, size, createConfig);
                if (bitmap != null) {
                    ivCode.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this, "二维码生成失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btDecode:
                if (bitmap == null) {
                    Toast.makeText(this, "请生成二维码再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                decodeBitmap(bitmap);
                break;

        }
    }

    private void decodeBitmap(Bitmap bitmap) {
        List<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        list.add(BarcodeFormat.AZTEC);
        list.add(BarcodeFormat.CODE_39);
        list.add(BarcodeFormat.CODE_93);
        list.add(BarcodeFormat.CODE_128);
        list.add(BarcodeFormat.DATA_MATRIX);
        list.add(BarcodeFormat.EAN_8);
        list.add(BarcodeFormat.EAN_13);
        list.add(BarcodeFormat.ITF);
        list.add(BarcodeFormat.PDF_417);
        list.add(BarcodeFormat.UPC_A);
        list.add(BarcodeFormat.UPC_E);
        list.add(BarcodeFormat.CODABAR);

        //Result result = DecodeUtils.startDecode(bitmap, barcodeFormat);
        /*解析某个或者多个二维码*/
        Result result = DecodeUtils.startDecode(bitmap, list);

        if (result == null) {
            Toast.makeText(this, "解析失败", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("解析结果");
        builder.setMessage(result.getText());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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
