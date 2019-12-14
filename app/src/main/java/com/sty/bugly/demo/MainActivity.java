package com.sty.bugly.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;

public class MainActivity extends AppCompatActivity {
    private TextView tvText;
    private Button btnCrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = findViewById(R.id.tv_text);
        tvText.setText("Hotfix V" + BuildConfig.VERSION_NAME);

        btnCrash = findViewById(R.id.btn_crash);
        btnCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashReport.testJavaCrash();
            }
        });
    }
}
