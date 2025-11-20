package com.example.masiwasi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.masiwasi.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(() -> {

            goHome();

        }, SPLASH_DELAY);
    }

    public void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
