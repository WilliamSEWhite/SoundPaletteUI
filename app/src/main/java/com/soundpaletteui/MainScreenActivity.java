package com.soundpaletteui.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Trending.SearchFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;
import com.soundpaletteui.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private Intent intent;
    private int userId;
    private UserModel user;

    private UserClient userClient;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize components before replacing the fragment
        initComponents();

        // Create the fragment with userId passed as an argument
        HomeFragment homeFragment = HomeFragment.newInstance(userId);
        ProfileFragment profileFragment = ProfileFragment.newInstance(userId);
        SearchFragment searchFragment = SearchFragment.newInstance(userId);

        replaceFragment(homeFragment);  // Replace fragment after passing the userId

        // Your existing bottom navigation setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int selected = item.getItemId();
            int navHome = R.id.nav_home;
            int navProfile = R.id.nav_profile;
            int navCreate = R.id.nav_create;
            int navMsg = R.id.nav_msg;
            int navSearch = R.id.nav_search;

            if(selected == navHome){
                Log.d("TAG- NavBar Home", "replaceFragment("+userId+")");
                replaceFragment(homeFragment);
                return true;
            }
            else if(selected == navProfile){
                Log.d("TAG- NavBar Profile", "replaceFragment("+userId+")");
                replaceFragment(profileFragment);
                return true;
            }
            else if(selected == navCreate){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View addPost = getLayoutInflater().inflate(R.layout.alert_create, null);
                builder.setView(addPost);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            else if(selected == navMsg){
                Log.d("TAG- NavBar Messages", "replaceFragment("+userId+")");
                replaceFragment(new MessageFragment());
                return true;
            }
            else if(selected == navSearch){
                Log.d("TAG- NavBar Search", "replaceFragment("+userId+")");
                replaceFragment(searchFragment);
                return true;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainScreenFrame, fragment);
        fragmentTransaction.commit();
    }

    /** initializes components in the activity */
    private void initComponents() {
        // Get the Intent that started this activity
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    private void getUser() {
        new GetUserAsync().execute();
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