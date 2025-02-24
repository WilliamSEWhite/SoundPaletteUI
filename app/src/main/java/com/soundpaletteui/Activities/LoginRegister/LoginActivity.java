package com.soundpaletteui.Activities.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;
import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Profile.Register;
import com.soundpaletteui.Infrastructure.ApiClients.LoginRegisterClient;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;
import pl.droidsonroids.gif.GifImageView;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.io.IOException;

/**
 * Manages user login and registration actions within the app.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginRegisterClient loginRegisterClient;
    UserModel user;
    EditText usernameBox;
    EditText passwordBox;
    FrameLayout frameRegister;
    GifImageView gifRegister;
    TextView registerText;
    FrameLayout frameLogin;
    GifImageView gifLogin;
    TextView loginText;
    String username;
    String password;

    /**
     * Sets up the activity and initializes UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UISettings.applyWhiteTopHueGradientBackground(findViewById(R.id.root_layout), 330f);
        initComponents();
        animateShadow();
    }

    /**
     * Animates the shadow of the main title text over time.
     */
    private void animateShadow() {
        final TextView appTitle = findViewById(R.id.textLogo);
        final int shadowColor = getResources().getColor(R.color.white);
        final float initialRadius = 2f;
        final float finalRadius = 5f;
        final float[] currentRadius = {initialRadius};
        final float[] currentDx = {2f};
        final float[] currentDy = {2f};

        Runnable updateShadow = () -> {
            appTitle.setShadowLayer(currentRadius[0], currentDx[0], currentDy[0], shadowColor);
        };

        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(initialRadius, finalRadius);
        radiusAnimator.setDuration(120000);
        radiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        radiusAnimator.addUpdateListener(animation -> {
            currentRadius[0] = (float) animation.getAnimatedValue();
            updateShadow.run();
        });
        radiusAnimator.start();

        ValueAnimator offsetAnimator = ValueAnimator.ofFloat(0f, 1f);
        offsetAnimator.setDuration(120000);
        offsetAnimator.setRepeatCount(ValueAnimator.INFINITE);
        offsetAnimator.setInterpolator(new LinearInterpolator());
        offsetAnimator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            currentDx[0] = 2f + (float)Math.sin(2 * Math.PI * fraction) * 5f;
            currentDy[0] = 2f + (float)Math.cos(2 * Math.PI * fraction) * 5f;
            updateShadow.run();
        });
        offsetAnimator.start();
    }

    /**
     * Initializes UI components and listeners for login/registration.
     */
    private void initComponents() {
        loginRegisterClient = SPWebApiRepository.getInstance().getLoginRegisterClient();
        usernameBox = findViewById(R.id.username);
        passwordBox = findViewById(R.id.password);
        frameRegister = findViewById(R.id.frame_register);
        gifRegister = findViewById(R.id.gif_register);
        registerText = findViewById(R.id.register_text);
        frameLogin = findViewById(R.id.frame_login);
        gifLogin = findViewById(R.id.gif_login);
        loginText = findViewById(R.id.login_text);
        frameRegister.setOnClickListener(v -> register());
        frameLogin.setOnClickListener(v -> {
            try {
                login();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Initiates the user registration process.
     */
    private void register(){
        new RegisterUserAsync().execute();
    }

    /**
     * Initiates the user login process.
     */
    private void login() throws IOException {
        new LoginUserAsync().execute();
    }

    /**
     * Displays feedback and navigates to next screen if login is successful.
     */
    void loginUser(){
        if(user != null) {
            Toast.makeText(this, "User logged in with Id " + user.getId(),
                    Toast.LENGTH_SHORT).show();
            nextActivity(user.getId(), 2);
        } else {
            Toast.makeText(this, "Failed to log in user", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays feedback and navigates to next screen if registration is successful.
     */
    void registerUser(){
        if(user != null) {
            Toast.makeText(this, "User registered with Id " + user.getId(),
                    Toast.LENGTH_SHORT).show();
            nextActivity(user.getId(), 1);
        } else {
            Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles user login in background.
     */
    private class LoginUserAsync extends AsyncTask<Void,Void, Void> {
        /**
         * Performs the login by calling the client in background.
         */
        @Override
        protected Void doInBackground(Void... d) {
            username = usernameBox.getText().toString();
            password = passwordBox.getText().toString();
            try {
                user = loginRegisterClient.loginUser(username, password);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        /**
         * Updates the UI after login attempt.
         */
        @Override
        protected void onPostExecute(Void v) {
            loginUser();
        }
    }

    /**
     * Handles user registration in background.
     */
    private class RegisterUserAsync extends AsyncTask<Void,Void, Void> {
        /**
         * Performs the registration by calling the client in background.
         */
        @Override
        protected Void doInBackground(Void... d) {
            username = usernameBox.getText().toString();
            password = passwordBox.getText().toString();
            try {
                user = loginRegisterClient.registerUser(username, password);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        /**
         * Updates the UI after registration attempt.
         */
        @Override
        protected void onPostExecute(Void v) {
            registerUser();
        }
    }

    /**
     * Navigates to the next appropriate activity based on actionId.
     */
    private void nextActivity(int userId, int actionId) {
        Intent intent;
        switch(actionId) {
            case 1:
                intent = new Intent(LoginActivity.this, Register.class);
                break;
            case 2:
                intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                break;
            default:
                return;
        }
        intent.putExtra("userId", user.getId());
        startActivity(intent);
        finish();
    }
}
