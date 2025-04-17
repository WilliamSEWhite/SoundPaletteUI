package com.soundpaletteui.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import com.soundpaletteui.Activities.Profile.RegisterActivity;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.SplashEmojiView;
import com.soundpaletteui.SPApiServices.ApiClients.LoginRegisterClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2500; // 5 seconds pause
    private boolean isLoggedIn = false;
    private final AppSettings appSettings = AppSettings.getInstance();

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
                getCredentials();
            }
        }, SPLASH_DISPLAY_LENGTH);
//        getCredentials();
    }

    private void skipSplash() {
        // Skip splash and go directly to LoginActivity
        getCredentials();
    }

    private void getCredentials(){
        String username = AppSettings.getUsernameValue(this);
        String password = AppSettings.getPasswordValue(this);

        if(!Objects.equals(username, "") && !Objects.equals(password, "")){
            new SplashActivity.LoginUserAsync().execute(username, password);
        }
        else {
            Toast.makeText(this, "Please login ",
                    Toast.LENGTH_SHORT).show();
            nextActivity(2);        }
    }

    void loginUser(){
        UserModel user = appSettings.getUser();
        if(user != null) {
            if(user.getUserInfo() != null){
                Toast.makeText(this, "User logged in with Id ",
                        Toast.LENGTH_SHORT).show();
                nextActivity(3);
            }
            else{
                Toast.makeText(this, "Please finish registration ",
                        Toast.LENGTH_SHORT).show();
                nextActivity(1);
            }
        }
        else {
            Toast.makeText(this, "Please login ",
                    Toast.LENGTH_SHORT).show();
            nextActivity(2);        }

    }

    /** move UX to next activity */
    private void nextActivity(int aId) {
        Intent i = null;
        switch(aId) {
            case 1:
                i = new Intent(SplashActivity.this, RegisterActivity.class);
                break;
            case 2:
//                i = new Intent(LoginActivity.this, HomeActivity.class);
                i = new Intent(SplashActivity.this, LoginActivity.class);
                break;
            case 3:
//                i = new Intent(LoginActivity.this, HomeActivity.class);
                i = new Intent(SplashActivity.this, MainScreenActivity.class);
                break;
        }
        startActivity(i);
        finish();
    }

    // AsyncTask for logging in an existing user
    private class LoginUserAsync extends AsyncTask<String, Void, Void> {

        @Override
        // Background task to log in the user
        protected Void doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                LoginRegisterClient client = SPWebApiRepository.getInstance().getLoginRegisterClient();

                // Call the loginUser method of the API client to log in the user
                appSettings.setUser(client.loginUser(username, password));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        // Handle the result of the login process
        protected void onPostExecute(Void o) {
            loginUser();
        }
    }

    private void navigateToLogin() {
        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
