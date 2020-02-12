package com.test.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
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
import com.github.selectcolordialog.SelectColorDialog;
import com.github.selectcolordialog.SelectColorListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

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


    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.H;

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

                Bitmap logoBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
                Bitmap bitmap = makeCodeBitmap(content, logoBitmap);
                ivCode.setImageBitmap(bitmap);
                break;
        }
    }

    private Bitmap makeCodeBitmap(String content, Bitmap logoBitmap) {
        int size = dp2px(this, 270);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {

            CreateConfig createConfig = new CreateConfig();
            createConfig.errorCorrection = errorCorrectionLevel;
            createConfig.setMargin(margin);
            createConfig.setIconBackgroundColor(iconBackgroundColor);
            createConfig.setIconForegroundColor(iconForegroundColor);
            createConfig.setIconCorner(iconCorner);
            createConfig.setIconImageCorner(imageCorner);
            createConfig.setIconWidth(iconWidth);
            createConfig.setIconMargin(iconMargin);

//            createConfig.setQrVersion(3);

            Map<EncodeHintType, Object> HINTS = new EnumMap<>(EncodeHintType.class);
            HINTS.put(EncodeHintType.CHARACTER_SET, createConfig.getCharacterSet());
            HINTS.put(EncodeHintType.ERROR_CORRECTION, createConfig.errorCorrection);
            HINTS.put(EncodeHintType.MARGIN, createConfig.getMargin());
            HINTS.put(EncodeHintType.QR_VERSION, createConfig.getQrVersion());

            BitMatrix encode = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, size, size, HINTS);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (encode.get(y, x)) {
                        pixels[y * size + x] = foregroundColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);

            if (cbAddIcon.isChecked() && logoBitmap != null) {

                Canvas canvas = new Canvas(bitmap);

                Matrix matrix = new Matrix();

                //二维码宽度和高度
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                /*不包含iconMargin的图片宽高*/
                int imageW = logoBitmap.getWidth();
//                int imageH = logoBitmap.getHeight();


                /*贴在二维码上面的图片宽高*/
                int iconW = createConfig.getIconWidth();
                if (iconW <= 0) {
                    iconW = imageW;
                }
                imageW = iconW - iconMargin * 2;
//                imageH = iconW - iconMargin * 2;

                float scale = 1f * imageW / logoBitmap.getWidth();

                int iconH = (int) (logoBitmap.getHeight() * scale + iconMargin * 2);

                float iconLeft = (width - iconW) / 2;
                float iconTop = (height - iconH) / 2;

                matrix.setTranslate((width - logoBitmap.getWidth()) / 2, (height - logoBitmap.getHeight()) / 2);
                matrix.postScale(scale, scale, width / 2, height / 2);

                if (createConfig.getIconBackgroundColor() != Color.TRANSPARENT) {
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(createConfig.getIconBackgroundColor());
                    paint.setStyle(Paint.Style.FILL);
                    RectF rectF = new RectF(iconLeft, iconTop, iconLeft + iconW, iconTop + iconH);
                    canvas.drawRoundRect(rectF, createConfig.getIconCorner(), createConfig.getIconCorner(), paint);
                }

                //如果小于零,图片圆角自适应icon圆角
                if (createConfig.getIconImageCorner() < 0) {
                    createConfig.setIconImageCorner(createConfig.getIconCorner() * imageW / iconW);
                }
                //如果图片也需要圆角
                int iconImageCorner = createConfig.getIconImageCorner();
                if (iconImageCorner > 0) {
                    int count = canvas.saveLayer(iconLeft, iconTop, iconLeft + iconW, iconTop + iconH, null, Canvas.ALL_SAVE_FLAG);

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//                    paint.setColor(Color.TRANSPARENT);
                    Path roundPath = new Path();
                    RectF rectF = new RectF(0, 0, logoBitmap.getWidth(), logoBitmap.getHeight());
                    matrix.mapRect(rectF);
                    roundPath.addRoundRect(rectF
                            , createConfig.getIconImageCorner(), createConfig.getIconImageCorner()
                            , Path.Direction.CW);

                    canvas.drawPath(roundPath, paint);

                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(logoBitmap, matrix, paint);

                    canvas.restoreToCount(count);
                } else {
                    canvas.drawBitmap(logoBitmap, matrix, null);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
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
