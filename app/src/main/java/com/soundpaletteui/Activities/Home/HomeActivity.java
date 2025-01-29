package com.soundpaletteui.Activities.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.MainTestApiClient;
import com.soundpaletteui.Infrastructure.ApiEndpoints.MainContentApiTest;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import com.soundpaletteui.R;

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
    private String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponents();
    }

    private void initComponents() {
        // Get the Intent that started this activity
        intent = getIntent();
        user = intent.getStringExtra("Username");
        pass = intent.getStringExtra("Password");

        recyclerView = findViewById(R.id.mainContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        recyclerView.setAdapter(mainContentAdapter);

        fetchUser();
    }

    private void fetchUser() {

        MainContentApiTest loginApi = MainTestApiClient.getRetrofitInstance()
                .create(MainContentApiTest.class);
        Call<UserModel> call = loginApi.loginUser(user, pass);
        // print GET URL for testing
        System.out.println("Request URL: " + call.request().url());
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if(response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.add(response.body());
                    mainContentAdapter.notifyDataSetChanged();
                }
                else {
                    System.out.println("Response Code: " + response.code());
                    System.out.println("Response Message: " + response.message());
                    System.out.println("Response Error Body: " + response.errorBody());
                    System.out.println("Response Body: " + response.body());
                    Toast.makeText(HomeActivity.this, "No data found!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: "
                        + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}