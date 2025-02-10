package com.soundpaletteui.Activities.Profile;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;
    private String userName;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // Static method to create a new instance of ProfileFragment with userId
    public static ProfileFragment newInstance(int userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId); // Add userId to the Bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1); // Default to -1 if not found
        }
        //Log.d("TAG - ProfileFragment", "getting username...");
        //userName = user.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView profile_bio = rootView.findViewById(R.id.profile_bio);
        profile_bio.setText("HI! This is the profile page for user ID number " +userId+ " but later we will fill it with the database bio");

        int colour_pressed = ContextCompat.getColor(requireContext(), R.color.button_pressed);
        int colour_default = ContextCompat.getColor(requireContext(), R.color.button_default);

        Button buttonPosts = rootView.findViewById(R.id.button_posts);
        Button buttonSaved = rootView.findViewById(R.id.button_saved);
        replaceFragment(userId);

        // Set onClickListener for buttonPosts
        buttonPosts.setOnClickListener(v -> {
            buttonPosts.setBackgroundTintList(ColorStateList.valueOf(colour_pressed));
            buttonSaved.setBackgroundTintList(ColorStateList.valueOf(colour_default));
            replaceFragment(userId);
        });

        // Set onClickListener for buttonSaved
        buttonSaved.setOnClickListener(v -> {

            buttonSaved.setBackgroundTintList(ColorStateList.valueOf(colour_pressed));
            buttonPosts.setBackgroundTintList(ColorStateList.valueOf(colour_default));
            replaceFragment(userId+1);
        });


        return rootView;
    }

    /** initializes components in the fragment */
    private void initComponents(View view) {
        // Get arguments instead of Intent
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", 0);
        }

        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);

        //recyclerView = view.findViewById(R.id.mainContent);
        //recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        //recyclerView.setAdapter(mainContentAdapter);

        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    private void getUser() {
        new Thread(() -> {
            try {
                user = userClient.getUser(userId);
                requireActivity().runOnUiThread(this::populateView);
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error fetching user", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void populateView() {
        userList.clear();
        userList.add(user);
        mainContentAdapter.notifyDataSetChanged();
    }


    private void replaceFragment(int userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(userId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

}
