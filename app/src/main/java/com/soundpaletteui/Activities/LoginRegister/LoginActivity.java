package com.soundpaletteui.Activities.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Profile.RegisterActivity;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.ApiClients.LoginRegisterClient;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import pl.droidsonroids.gif.GifImageView;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Manages user login and registration actions within the app.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginRegisterClient loginRegisterClient;
    private final AppSettings appSettings = AppSettings.getInstance();

    private UserModel user;
    private EditText usernameBox;
    private EditText passwordBox;
    private Button registerBtn;
    private Button loginBtn;
    private String username;
    private String password;
    FrameLayout frameRegister;
    GifImageView gifRegister;
    TextView registerText;
    FrameLayout frameLogin;
    GifImageView gifLogin;
    TextView loginText;

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
        frameLogin.setOnClickListener(v -> login());
    }

    /** register new user */
    private void register() {
        // Retrieve the username and password entered by the user
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Validation checks for username and password
        if (!isValidUsername(username) || !isValidPassword(password)) {
            return;
        }

        // If validation passes, call the async task for user registration
        new RegisterUserAsync().execute(username, password);
    }

    /** login existing user */
    private void login() {
        // Retrieve the username and password entered by the user
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Validation checks for username and password
        if (!isValidUsername(username) || !isValidPassword(password)) {
            return;
        }

        // If validation passes, call the async task for user login
        new LoginUserAsync().execute(username, password);
    }

    // Validation for username
    public boolean isValidUsername(String username) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() > 20) {
            Toast.makeText(this, "Username should be less than 20 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Validation for password
    public boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, "Password should be between 6-20 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()].*")) {
            Toast.makeText(this, "Password must contain a number and a special character", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void loginUser() {
        UserModel user = appSettings.getUser();
        if (user != null) {
            if (user.getUserInfo() != null) {
                Toast.makeText(this, "User logged in with Id " + user.getUserId(), Toast.LENGTH_SHORT).show();
                nextActivity(2);
            } else {
                Toast.makeText(this, "Please finish registration " + user.getUserId(), Toast.LENGTH_SHORT).show();
                nextActivity(1);
            }
        } else {
            Toast.makeText(this, "Failed to log in user", Toast.LENGTH_SHORT).show();
        }
    }

    void registerUser() {
        UserModel user = appSettings.getUser();
        if (user != null) {
            Toast.makeText(this, "User registered with Id " + user.getUserId(), Toast.LENGTH_SHORT).show();
            nextActivity(1);
        } else {
            Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextActivity(int aId) {
        Intent i = null;
        switch (aId) {
            case 1:
                i = new Intent(LoginActivity.this, RegisterActivity.class);
                break;
            case 2:
                i = new Intent(LoginActivity.this, MainScreenActivity.class);
                break;
        }
        startActivity(i);
        finish();
    }

    // AsyncTask for registering a new user
    private class RegisterUserAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                appSettings.setUser(loginRegisterClient.registerUser(username, password));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            registerUser();
        }
    }

    // AsyncTask for logging in an existing user
    private class LoginUserAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                appSettings.setUser(loginRegisterClient.loginUser(username, password));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            loginUser();
        }
    }
}