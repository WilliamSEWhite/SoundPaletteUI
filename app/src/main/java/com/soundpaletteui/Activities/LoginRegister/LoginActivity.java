package com.soundpaletteui.Activities.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Profile.RegisterActivity;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.ApiClients.LoginRegisterClient;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Models.UserModel;

public class LoginActivity extends AppCompatActivity {

    // Declare the required variables for login and registration
    private LoginRegisterClient loginRegisterClient;
    private UserModel user;
    private EditText usernameBox;
    private EditText passwordBox;
    private Button registerBtn;
    private Button loginBtn;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize the components of the activity
        initComponents();
    }

    // Initialize the components (UI elements) in the layout
    private void initComponents() {
        // Get the loginRegisterClient instance from SPWebApiRepository
        loginRegisterClient = SPWebApiRepository.getInstance().getLoginRegisterClient();
        // Initialize the EditText fields and Buttons from the layout
        usernameBox = findViewById(R.id.username);
        passwordBox = findViewById(R.id.password);

        // Register Button
        registerBtn = findViewById(R.id.register_button);
        // Login Button
        loginBtn = findViewById(R.id.login_button);

        // Set click listener for the Register Button to call the register method
        registerBtn.setOnClickListener(v -> register());
        // Set click listener for the Login Button to call the login method
        loginBtn.setOnClickListener(v -> login());
    }

    // Register method to handle user registration
    private void register() {
        // Retrieve the username and password entered by the user
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Validation checks for username and password
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Username length validation (should not be more than 20 characters)
        if (username.length() > 20) {
            Toast.makeText(this, "Username should be less than 20 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password validation (should be between 8-10 characters and contain numbers and special characters)
        if (!password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()].*") || password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, "Password must be 6-20 characters, contain numbers and a special character", Toast.LENGTH_SHORT).show();
            return;
        }

        // If validation passes, call the async task for user registration
        new RegisterUserAsync().execute(username, password);
    }

    // Login method to handle user login
    private void login() {
        // Retrieve the username and password entered by the user
        String username = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();

        // Validation checks for username and password
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // If validation passes, call the async task for user login
        new LoginUserAsync().execute(username, password);
    }

    // AsyncTask for registering a new user
    private class RegisterUserAsync extends AsyncTask<String, Void, UserModel> {

        @Override
        // Background task to register the user
        protected UserModel doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                // Call the registerUser method of the API client to register the user
                return loginRegisterClient.registerUser(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        // Handle the result of the registration process
        protected void onPostExecute(UserModel user) {
            if (user != null) {
                // Show success message and navigate to the Register activity or Home screen
                Toast.makeText(LoginActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                nextActivity(user.getId(), 1); // Pass user ID and action code to nextActivity
            } else {
                // Show failure message
                Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask for logging in an existing user
    private class LoginUserAsync extends AsyncTask<String, Void, UserModel> {

        @Override
        // Background task to log in the user
        protected UserModel doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                // Call the loginUser method of the API client to log in the user
                return loginRegisterClient.loginUser(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        // Handle the result of the login process
        protected void onPostExecute(UserModel user) {
            if (user != null) {
                // Show success message and navigate to the Home screen
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                nextActivity(user.getId(), 2); // Pass user ID and action code to nextActivity
            } else {
                // Show failure message
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper method to navigate to the next activity (Home/Register)
    private void nextActivity(int userId, int action) {
        Intent intent = null;
        // Choose activity based on the action passed (1 for Register, 2 for Home)
        if (action == 1) {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainScreenActivity.class);
        }
        // Pass user ID to the next activity
        intent.putExtra("userId", userId);
        startActivity(intent); // Start the next activity
        finish(); // Finish the current activity
    }
}
