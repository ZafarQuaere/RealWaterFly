package com.zaf.waterfly.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.zaf.waterfly.R;

public class UserRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
    }

    public void registerUser(View view) {
        TextInputEditText tieMobile = findViewById(R.id.tieMobile);
        TextInputEditText tieOtp = findViewById(R.id.tieOtp);
        TextInputEditText tiePin = findViewById(R.id.tiePin);
        validateInputs(tieMobile.getText().toString(),tieOtp.getText().toString(),tiePin.getText().toString());
    }

    private void validateInputs(String mobile, String otp, String pin) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_drawer,menu);
        return super.onCreateOptionsMenu(menu);
    }
}