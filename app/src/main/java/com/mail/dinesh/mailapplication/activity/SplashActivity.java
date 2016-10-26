package com.mail.dinesh.mailapplication.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mail.dinesh.mailapplication.R;
import com.mail.dinesh.mailapplication.conf.Configuration;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        waitForTime(Configuration.SPLASH_DISPLAY_LENGTH);
    }

    protected void waitForTime(int waitTime)
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, waitTime);
    }
}
