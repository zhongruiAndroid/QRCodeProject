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

public class MakeCodeActivity extends AppCompatActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView ivCode;
    private RadioGroup rg;
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

                /*生成二维码对应的参数*/
                CreateConfig createConfig = new CreateConfig();
                /*生成qrcode时，二维码的容错率，默认ErrorCorrectionLevel.H(30%)*/
                createConfig.errorCorrection = errorCorrectionLevel;
                /*二维码的背景色，默认白色*/
                createConfig.setBackgroundColor(backgroundColor);
                /*二维码前景色，默认黑色*/
                createConfig.setForegroundColor(foregroundColor);
                /*二维码内容距离二维码图片的边距*/
                createConfig.setMargin(margin);
                /*给二维码添加图片时的背景色*/
                createConfig.setIconBackgroundColor(iconBackgroundColor);
                /*给二维码添加图片时生成icon的圆角*/
                createConfig.setIconCorner(iconCorner);
                /*给二维码添加图片的圆角*/
                createConfig.setIconImageCorner(imageCorner);
                /*给二维码添加图片生成出来的icon宽度*/
                createConfig.setIconWidth(iconWidth);
                /*给二维码添加图片生成出来的icon和图片间距*/
                createConfig.setIconMargin(iconMargin);
                /*生成什么格式的二维码*/
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
        Result result = DecodeUtils.startDecode(bitmap, BarcodeFormat.QR_CODE);
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
