package com.soundpaletteui.Main.Activities.apiTest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.soundpaletteui.Main.Activities.apiTest.apiClients.LoginApiClient;
import com.soundpaletteui.Main.Activities.apiTest.interfaces.LoginApi;
import com.soundpaletteui.Main.Activities.apiTest.models.User;
import com.soundpaletteui.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {

        recyclerView = findViewById(R.id.mainContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        recyclerView.setAdapter(mainContentAdapter);

        fetchUser();
    }

    private void fetchUser() {

        LoginApi loginApi = LoginApiClient.getRetrofitInstance().create(LoginApi.class);
        Call<User> call = loginApi.loginUser("testUser", "testPass");
        // print GET URL for testing
        System.out.println("Request URL: " + call.request().url());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
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
                    Toast.makeText(MainActivity.this, "No data found!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: "
                + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}