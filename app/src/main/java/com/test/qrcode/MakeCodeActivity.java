package com.test.qrcode;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.selectcolordialog.SelectColorDialog;
import com.github.selectcolordialog.SelectColorListener;

public class MakeCodeActivity extends AppCompatActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener {

    private RadioGroup rg;
    private SeekBar sbMargin;
    private TextView tvForegroundColor;
    private TextView tvBackgroundColor;
    private CheckBox cbAddIcon;
    private SeekBar sbIconMargin;
    private SeekBar sbIconCorner;
    private TextView tvIconForegroundColor;
    private TextView tvIconBackgroundColor;
    private EditText etCodeContent;
    private Button btMake;


    private int margin;
    private int foregroundColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private int iconMargin;
    private int iconCorner;
    private int iconForegroundColor = Color.BLACK;
    private int iconBackgroundColor = Color.WHITE;
    private SelectColorDialog selectColorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_code);
        initView();
    }

    private void initView() {
        rg = findViewById(R.id.rg);
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
                break;
        }
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
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
