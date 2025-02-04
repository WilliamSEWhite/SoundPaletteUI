package com.soundpaletteui.Activities.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Profile.Register;
import com.soundpaletteui.Infrastructure.ApiClients.LoginRegisterClient;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.R;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private LoginRegisterClient loginRegisterClient;
    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button registerBtn = findViewById(R.id.register_button);
        registerBtn.setOnClickListener(v -> register());
        Button loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(v -> {
            try {
                login();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        loginRegisterClient = SPWebApiRepository.getInstance().getLoginRegisterClient();
    }

    private void register(){
        new RegisterUserAsync().execute();
    }
    private void login() throws IOException {
        new LoginUserAsync().execute();
    }

    void loginUser(){
        if(user != null) {
            Toast.makeText(this, "User logged in with Id " + user.getId(),
                    Toast.LENGTH_SHORT).show();
            nextActivity(user.getId(), 2);
        }
        else {
            Toast.makeText(this, "Failed to log in user", Toast.LENGTH_SHORT).show();
        }

    }
    void registerUser(){
        if(user != null) {
            Toast.makeText(this, "User registered with Id " + user.getId(),
                    Toast.LENGTH_SHORT).show();
            nextActivity(user.getId(), 1);
        }
        else {
            Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginUserAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            EditText usernameBox = findViewById(R.id.username);
            EditText passwordBox = findViewById(R.id.password);
            try {
                user = loginRegisterClient.loginUser(usernameBox.getText().toString(),
                        passwordBox.getText().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            loginUser();
        }//end onPostExecute
    }
    private class RegisterUserAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            EditText usernameBox = findViewById(R.id.username);
            EditText passwordBox = findViewById(R.id.password);
            try {
                user = loginRegisterClient.registerUser(usernameBox.getText().toString(),
                        passwordBox.getText().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            registerUser();
        }//end onPostExecute
    }

    /* not a fan of redundant code :)
    private void homeActivity(int Id) {
//        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        Intent i = new Intent(LoginActivity.this, MainScreenActivity.class);
        Bundle param = new Bundle();
        param.putInt("userId", user.getId());
        i.putExtras(param);
        startActivity(i);
        finish();
    }
    */

    private void nextActivity(int Id, int aId) {
        Intent i = null;
        switch(aId) {
            case 1:
                i = new Intent(LoginActivity.this, Register.class);
                break;
            case 2:
//                i = new Intent(LoginActivity.this, HomeActivity.class);
                i = new Intent(LoginActivity.this, MainScreenActivity.class);
                break;
        }
        Bundle param = new Bundle();
        param.putInt("userId", user.getId());
        i.putExtras(param);
        startActivity(i);
        finish();
    }

}