package com.soundpaletteui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.SplashEmojiView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 10000; // 5 seconds pause

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SplashEmojiView splashEmojiView = findViewById(R.id.splashEmojiBackground);

        // Click listener to skip the splash and go directly to the LoginActivity
        splashEmojiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipSplash();
            }
        });

        // Delay handler for normal splash behavior
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToLogin();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void skipSplash() {
        // Skip splash and go directly to LoginActivity
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
