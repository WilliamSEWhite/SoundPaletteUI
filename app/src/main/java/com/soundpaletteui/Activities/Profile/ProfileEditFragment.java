package com.soundpaletteui.Activities.Profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileEditFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;
    private String userName;
    public ProfileEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        TextView profile_user = rootView.findViewById(R.id.profile_user);
        TextView profile_email = rootView.findViewById(R.id.profile_email);
        TextView profile_phone = rootView.findViewById(R.id.profile_phone);
        TextView profile_bio = rootView.findViewById(R.id.profile_bio);
        //Spinner profile_location = rootView.findViewById(R.id.profile_location);

        Button buttonCancel = rootView.findViewById(R.id.btnCancel);
        Button buttonSave = rootView.findViewById(R.id.btnSave);


        buttonCancel.setOnClickListener(v -> {
            ProfileFragment profileFragment = ProfileFragment.newInstance(userId);

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, profileFragment);
            transaction.commit();
        });

        return rootView;
    }
    public static ProfileEditFragment newInstance(int userId) {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId); // Add userId to the Bundle
        fragment.setArguments(args);
        return fragment;
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

}
