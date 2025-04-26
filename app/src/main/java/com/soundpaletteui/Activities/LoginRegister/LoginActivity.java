package com.soundpaletteui.Activities.LoginRegister;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Profile.RegisterActivity;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.LoginRegisterClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import pl.droidsonroids.gif.GifImageView;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // UI components
    private EditText usernameBox;
    private EditText passwordBox;
    private FrameLayout frameRegister, frameLogin;
    private TextView registerText, loginText;
    private TextView loadingMessage;
    private ObjectAnimator circleAnimator;
    private boolean isAnimating = false;

    private EmojiBackgroundView emojiBackground;

    // Other fields
    private final AppSettings appSettings = AppSettings.getInstance();
    private final Handler patternHandler = new Handler();
    private Runnable patternRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UISettings.applyWhiteTopHueGradientBackground(findViewById(R.id.root_layout), 330f);
        emojiBackground = findViewById(R.id.emojiBackground);

        initComponents();
        animateShadow();
        startPatternLoop();
    }

    private void initComponents() {
        usernameBox = findViewById(R.id.username);
        passwordBox = findViewById(R.id.password);

        frameRegister = findViewById(R.id.frame_register);
        registerText = findViewById(R.id.register_text);

        frameLogin = findViewById(R.id.frame_login);
        loginText = findViewById(R.id.login_text);

        frameRegister.setOnClickListener(v -> register());
        frameLogin.setOnClickListener(v -> login());

        loadingMessage = findViewById(R.id.loadingMessage);
    }

    private void lockUI() {
        loadingMessage.setVisibility(View.VISIBLE);
        loadingMessage.setAlpha(0f);
        loadingMessage.animate().alpha(1f).setDuration(300).start();

        loadingMessage.animate()
                .rotationBy(360f)
                .setDuration(1500)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> {
                    if (loadingMessage.getVisibility() == View.VISIBLE) {
                        loadingMessage.setRotation(0f);
                        loadingMessage.animate().rotationBy(360f)
                                .setDuration(1500)
                                .setInterpolator(new LinearInterpolator())
                                .withEndAction(this::lockUI)
                                .start();
                    }
                })
                .start();

        frameLogin.setEnabled(false);
        frameRegister.setEnabled(false);
        usernameBox.setEnabled(false);
        passwordBox.setEnabled(false);
    }

    private void unlockUI() {
        loadingMessage.animate().cancel();
        loadingMessage.setRotation(0f);
        loadingMessage.setVisibility(View.GONE);

        frameLogin.setEnabled(true);
        frameRegister.setEnabled(true);
        usernameBox.setEnabled(true);
        passwordBox.setEnabled(true);
    }

    private class LoginUserAsync extends AsyncTask<String, Void, Void> {
        private String action;

        public LoginUserAsync(String action) {
            this.action = action;
        }

        @Override
        protected Void doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                LoginRegisterClient client = SPWebApiRepository.getInstance().getLoginRegisterClient();

                if (action.equals("register")) {
                    appSettings.setUser(client.registerUser(username, password));
                } else {
                    appSettings.setUser(client.loginUser(username, password));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            unlockUI();
            if (action.equals("register")) {
                registerUser();
            } else {
                loginUser();
            }
        }
    }

    private void register() {
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Edge case validation: Empty username/password
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Edge case validation: Username length check
        if (username.length() > 20) {
            Toast.makeText(this, "Username should be less than 20 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Edge case validation: Password strength check
        if (!password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()].*") || password.length() < 8 || password.length() > 20) {
            Toast.makeText(this, "Password must be 8-20 characters, contain numbers and a special character", Toast.LENGTH_SHORT).show();
            return;
        }

        lockUI();
        new LoginUserAsync("register").execute(username, password);
    }

    private void login() {
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Edge case validation: Empty username/password
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with login if inputs are valid
        lockUI();
        new LoginUserAsync("login").execute(username, password);
    }

    void loginUser() {
        UserModel user = appSettings.getUser();
        if (user != null) {
            AppSettings.setUsernameValue(this, user.getUsername());
            AppSettings.setPasswordValue(this, user.getPassword());

            if (user.getUserInfo() != null) {
                Toast.makeText(this, "User logged in with Id " + user.getUserId(), Toast.LENGTH_SHORT).show();
                nextActivity(user.getUserId(), 2);
            } else {
                Toast.makeText(this, "Please finish registration " + user.getUserId(), Toast.LENGTH_SHORT).show();
                nextActivity(user.getUserId(), 1);
            }
        } else {
            Toast.makeText(this, "Failed to log in user", Toast.LENGTH_SHORT).show();
        }
    }

    void registerUser() {
        UserModel user = appSettings.getUser();
        if (user != null) {
            AppSettings.setUsernameValue(this, user.getUsername());
            AppSettings.setPasswordValue(this, user.getPassword());
            Toast.makeText(this, "User registered with Id " + user.getUserId(), Toast.LENGTH_SHORT).show();
            nextActivity(user.getUserId(), 1);
        } else {
            Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextActivity(int userId, int action) {
        Intent intent = (action == 1)
                ? new Intent(this, RegisterActivity.class)
                : new Intent(this, MainScreenActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    private void animateShadow() {
        final TextView appTitle = findViewById(R.id.textLogo);
        final int shadowColor = getResources().getColor(R.color.white);
        final float[] currentRadius = {2f};
        final float[] currentDx = {2f};
        final float[] currentDy = {2f};

        Runnable updateShadow = () -> appTitle.setShadowLayer(currentRadius[0], currentDx[0], currentDy[0], shadowColor);

        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(2f, 5f);
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

    private void startPatternLoop() {
        patternRunnable = () -> {
            int[] patterns = { EmojiBackgroundView.PATTERN_GRID };
            int randomPattern = patterns[(int)(Math.random() * patterns.length)];
            if (emojiBackground != null) {
                emojiBackground.setPatternType(randomPattern);
            }
            patternHandler.postDelayed(patternRunnable, 1100);
        };

        patternHandler.post(patternRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        patternHandler.removeCallbacks(patternRunnable);
    }
}
