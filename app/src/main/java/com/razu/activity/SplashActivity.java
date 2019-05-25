package com.razu.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.razu.R;
import com.razu.helper.PreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private PreferencesManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new PreferencesManager(this);
        setContentView(R.layout.activity_splash);

        navigate();
    }

    private void navigate() {
        if (session.isFirstTimeLaunch()) {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.overridePendingTransition(0, 0);
            SplashActivity.this.finish();
        } else if (session.isSignIn()) {
            Intent intent = new Intent(SplashActivity.this, UserProcessingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.overridePendingTransition(0, 0);
            SplashActivity.this.finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_REVEAL_X, 10);
            intent.putExtra(MainActivity.EXTRA_REVEAL_Y, 10);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.overridePendingTransition(0, 0);
            SplashActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}