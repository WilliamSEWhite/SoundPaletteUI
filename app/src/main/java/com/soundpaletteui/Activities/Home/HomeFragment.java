package com.soundpaletteui.Activities.Home;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    // Static method to create a new instance of HomeFragment with userId
    public static HomeFragment newInstance(int userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId); // Pass userId with key "USER_ID"
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1); // Ensure correct key
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(rootView);

        int colour_pressed = ContextCompat.getColor(requireContext(), R.color.button_pressed);
        int colour_default = ContextCompat.getColor(requireContext(), R.color.button_default);

        Button buttonExplore = rootView.findViewById(R.id.button_explore);
        Button buttonFollower = rootView.findViewById(R.id.button_follower);

        Random random = new Random();
        int randomNumber = random.nextInt(6) + 10;
        Log.d("HomeFragment", "Initial randomNumber: " + randomNumber);
        replaceFragment(randomNumber);

        buttonExplore.setOnClickListener(v -> {
            buttonExplore.setBackgroundTintList(ColorStateList.valueOf(colour_pressed));
            buttonFollower.setBackgroundTintList(ColorStateList.valueOf(colour_default));

            int randomExploreNumber = random.nextInt(6) + 10;
            Log.d("HomeFragment", "Explore clicked - randomNumber: " + randomExploreNumber);
            replaceFragment(randomExploreNumber);
        });

        buttonFollower.setOnClickListener(v -> {
            buttonFollower.setBackgroundTintList(ColorStateList.valueOf(colour_pressed));
            buttonExplore.setBackgroundTintList(ColorStateList.valueOf(colour_default));

            int randomFollowerNumber = random.nextInt(9) + 1;
            Log.d("HomeFragment", "Follower clicked - randomNumber: " + randomFollowerNumber);
            replaceFragment(randomFollowerNumber);
        });

        return rootView;
    }


    private void initComponents(View view) {
        // Get arguments instead of Intent
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0); // Correct key
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
        if (user != null) {
            userList.clear();
            userList.add(user);
            mainContentAdapter.notifyDataSetChanged();
        }
    }


    private void replaceFragment(int userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(userId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }
}