package com.soundpaletteui.Activities.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
        EditText usernameBox = findViewById(R.id.username);
        EditText passwordBox = findViewById(R.id.password);

    }
    private void login() throws IOException {
        new LoginUserAsync().execute();
    }

    void loginUser(){

    }

    private class LoginUserAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            EditText usernameBox = findViewById(R.id.username);
            EditText passwordBox = findViewById(R.id.password);
            try {
                user = loginRegisterClient.loginUser(usernameBox.getText().toString(), passwordBox.getText().toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        //after player is updated. reload player
        protected void onPostExecute(Void v) {
            // go back to practice setup
            loginUser();
        }//end onPostExecute
    }
}