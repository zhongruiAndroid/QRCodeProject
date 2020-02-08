package com.test.qrcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String content = getIntent().getStringExtra("content");

        TextView tvContent = findViewById(R.id.tvContent);
        ImageView iv = findViewById(R.id.iv);
        tvContent.setText(" 扫描文字结果:" + content);
    }
}
