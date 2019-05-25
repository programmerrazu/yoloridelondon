package com.razu.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.razu.R;
import com.razu.helper.PreferencesManager;

import es.dmoral.toasty.Toasty;

public class UserProcessingActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private EditText etPassword, etEmail;
    private PreferencesManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_processing);
        session = new PreferencesManager(this);

        setUIComponent();
    }

    private void setUIComponent() {
        viewFlipper = (ViewFlipper) findViewById(R.id.vf_user_process);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        viewFlipper.setDisplayedChild(0);
    }

    public void onForgetPassword(View view) {
        viewFlipper.setDisplayedChild(1);
    }

    public void onGetSignUp(View view) {
        viewFlipper.setDisplayedChild(2);
    }

    public void onLogIn(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.length() < 3) {
            Toasty.info(getApplicationContext(), "Please provide valid email", Toast.LENGTH_SHORT, true).show();
        } else if (password.length() < 3) {
            Toasty.info(getApplicationContext(), "Please provide valid password", Toast.LENGTH_SHORT, true).show();
        } else {
            session.setEmail(email);
            session.setSignIn(false);
            navigate();
        }
    }

    private void navigate() {
        Intent intent = new Intent(UserProcessingActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_REVEAL_X, 10);
        intent.putExtra(MainActivity.EXTRA_REVEAL_Y, 10);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        UserProcessingActivity.this.startActivity(intent);
        UserProcessingActivity.this.overridePendingTransition(0, 0);
        UserProcessingActivity.this.finish();
    }

    public void onResetPassword(View view) {
        Toasty.info(getApplicationContext(), "Reset Password coming Soon", Toast.LENGTH_SHORT, true).show();
    }

    public void onSignUp(View view) {
        Toasty.info(getApplicationContext(), "Sign Up coming Soon", Toast.LENGTH_SHORT, true).show();
    }

    @Override
    public void onBackPressed() {
        onBackPress();
    }

    public void onBacks(View view) {
        onBackPress();
    }

    private void onBackPress() {
        int index = viewFlipper.getDisplayedChild();
        if (index > 0) {
            viewFlipper.setDisplayedChild(0);
        } else {
            finish();
        }
    }
}