package com.soundpaletteui.Activities.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private Intent intent;
    private int userId;
    private UserModel user;

    private UserClient userClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponents();
    }

    /** initializes components in the activity */
    private void initComponents() {
        // Get the Intent that started this activity
        intent = getIntent();
        userId = intent.getIntExtra("Id", 0);

        recyclerView = findViewById(R.id.mainContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        recyclerView.setAdapter(mainContentAdapter);

        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    private void getUser() {
        new HomeActivity.GetUserAsync().execute();

    }

    private void populateView(){
        userList.clear();
        userList.add(user);
    }

    private class GetUserAsync extends AsyncTask<Void,Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                user = userClient.getUser(userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }//end doInBackground

        protected void onPostExecute(Void v) {
            populateView();
        }//end onPostExecute
    }
}