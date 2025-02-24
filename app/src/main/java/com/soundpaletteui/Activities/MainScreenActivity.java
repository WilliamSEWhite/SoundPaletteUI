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
    private ActivityMainBinding binding;

    // Fragments
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding and set layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize components
        initComponents();

        // Initialize Fragments AFTER retrieving userId
        homeFragment = HomeFragment.newInstance(userId);
        profileFragment = ProfileFragment.newInstance(userId);
        searchFragment = SearchFragment.newInstance(userId);
        messageFragment = new MessageFragment();

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            replaceFragment(homeFragment);
        }

        // Bottom Navigation Setup
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int selected = item.getItemId();
            if (selected == R.id.nav_home) {
                Log.d("TAG- NavBar", "Home Selected");
                replaceFragment(homeFragment);
            } else if (selected == R.id.nav_profile) {
                Log.d("TAG- NavBar", "Profile Selected");
                replaceFragment(profileFragment);
            } else if (selected == R.id.nav_create) {
                showCreatePostDialog();
            } else if (selected == R.id.nav_msg) {
                Log.d("TAG- NavBar", "Messages Selected");
                replaceFragment(messageFragment);
            } else if (selected == R.id.nav_search) {
                Log.d("TAG- NavBar", "Search Selected");
                replaceFragment(searchFragment);
            }
            return true;
        });
    }

    /** Replaces fragment while retaining previous fragments */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainScreenFrame, fragment);
        fragmentTransaction.commit();
    }

    /** Shows the create post dialog */
    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View addPost = getLayoutInflater().inflate(R.layout.alert_create, null);
        builder.setView(addPost);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Initializes components in the activity */
    private void initComponents() {
        intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    /** Fetches user data asynchronously */
    private void getUser() {
        new GetUserAsync().execute();
    }

    /** Updates the view after fetching user data */
    private void populateView() {
        userList.clear();
        if (user != null) {
            userList.add(user);
            mainContentAdapter.notifyDataSetChanged(); // Notify adapter
        }
    }

    /** AsyncTask for fetching user data */
    private class GetUserAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                user = userClient.getUser(userId);
            } catch (IOException e) {
                Log.e("TAG", "Error fetching user", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            populateView();
        }
    }
}