package com.soundpaletteui.Activities.Profile;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.widget.Button;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Activities.Home.HomeFragment;
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
        profile_bio.setText("HI! I am Lucy and I am an art instructor located in California. Books are closed for February! Check back for March's availability.");

        int colour_pressed = ContextCompat.getColor(requireContext(), R.color.button_pressed);
        int colour_default = ContextCompat.getColor(requireContext(), R.color.button_default);

        Button buttonPosts = rootView.findViewById(R.id.button_posts);
        Button buttonSaved = rootView.findViewById(R.id.button_saved);
        AppCompatImageButton buttonEdit = rootView.findViewById(R.id.edit_profile_button);

        // Set onClickListener for buttonPosts
        buttonPosts.setOnClickListener(v -> {
            buttonPosts.setBackgroundTintList(ColorStateList.valueOf(colour_pressed));
            buttonSaved.setBackgroundTintList(ColorStateList.valueOf(colour_default));

            setButtonTextUnderline(buttonPosts, true);
            setButtonTextUnderline(buttonSaved, false);

            replaceFragment(1);
        });

        // Set onClickListener for buttonSaved
        buttonSaved.setOnClickListener(v -> {
            buttonSaved.setBackgroundTintList(ColorStateList.valueOf(colour_pressed));
            buttonPosts.setBackgroundTintList(ColorStateList.valueOf(colour_default));

            setButtonTextUnderline(buttonSaved, true);
            setButtonTextUnderline(buttonPosts, false);

            replaceFragment(2);
        });

        buttonEdit.setOnClickListener(v -> {
            ProfileEditFragment profileEditFragment = ProfileEditFragment.newInstance(userId);

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, profileEditFragment);
            transaction.commit();
        });


        buttonPosts.performClick();
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

    private void setButtonTextUnderline(Button button, boolean isSelected) {
        String text = button.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        if (isSelected) {
            spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            button.setTypeface(null, Typeface.BOLD); // Make text bold for emphasis
        } else {
            button.setTypeface(null, Typeface.NORMAL); // Reset style
        }

        button.setText(spannableString);
    }

}
