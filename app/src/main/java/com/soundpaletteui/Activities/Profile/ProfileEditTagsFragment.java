package com.soundpaletteui.Activities.Profile;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.soundpaletteui.Infrastructure.Adapters.TagSelectAdapter;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Handles editing the user's tags inside their profile settings.
// Shows a list of available tags, lets the user select them, and saves the changes.
public class ProfileEditTagsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TagSelectAdapter adapter;
    private List<TagModel> globalTags;
    private List<TagModel> userTags;
    private TagClient tagClient;
    private UserModel user;
    private Button btnDone;
    private int fragId;
    private ShapeableImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user_tags, container, false);
        if(getArguments() != null) {
            fragId = getArguments().getInt("nav", 0);
        }
        // Apply dark mode gradient background
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(view.getContext());
        UISettings.applyBrightnessGradientBackground(view, 50f, isDarkMode);

        // Load the user's profile picture
        loadProfileImage();

        // Set up buttons, recycler view, and other components
        initComponents(view);
        return view;
    }

    // Sets up the views and loads initial data
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        tagClient = SPWebApiRepository.getInstance().getTagClient();

        recyclerView = view.findViewById(R.id.userTagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnDone = view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> done());
        imageView = view.findViewById(R.id.profile_picture);

        globalTags = new ArrayList<>();
        userTags = new ArrayList<>();

        // Start loading tags
        getTags();
    }

    // Loads the user's profile picture
    private void loadProfileImage() {
        new Thread(() -> {
            // update UI on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                ImageUtils.getProfileImageByUsername(AppSettings.getInstance().getUsername(),
                        imageView,
                        requireContext());
            });
        }).start();
    }

    // Called when the user taps the Done button
    private void done() {
        // If there are no tags selected, show a message
        if (globalTags == null || globalTags.isEmpty()) {
            Toast.makeText(requireActivity(), "No tags were selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(adapter == null) {
            Toast.makeText(requireActivity(), "No tags were selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect all the tags that the user selected
        List<TagModel> selectedTags = new ArrayList<>();
        for(TagModel tag : globalTags) {
            if(tag.isSelected()) {
                selectedTags.add(tag);
            }
        }

        // Collect all the tags that the user selected
        new Thread(() -> {
            try {
                tagClient.updateTags(user.getUserId(), selectedTags);
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Tags saved successfully",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                Toast.makeText(requireActivity(), "Error saving tags",
                        Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }).start();

        // Navigate the user back to the correct screen after saving
        Bundle args = new Bundle();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        switch(fragId) {
            case 0:
                Navigation.replaceFragment(fragmentManager, new ProfileFragment(),
                        "PROFILE_FRAGMENT", R.id.mainScreenFrame);
                break;
            case 1:
                args.putParcelableArrayList("selectedTags", new ArrayList<>(selectedTags));
                Navigation.replaceFragment(fragmentManager, new ProfileEditFragment(),
                        "PROFILE_EDIT_FRAGMENT", R.id.mainScreenFrame);
                break;
        }
    }

    // Fetches all available tags and the user's selected tags
    private void getTags() {
        new Thread(() -> {
            try {
                globalTags = tagClient.getTags();
                userTags = tagClient.getUserTags(user.getUserId());
                for(TagModel globalTag : globalTags) {
                    for(TagModel userTag : userTags) {
                        if(globalTag.getTagId() == userTag.getTagId()) {
                            globalTag.setSelected(true);
                        }
                    }
                }

                // Update the UI with the tag list
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        adapter = new TagSelectAdapter((ArrayList<TagModel>) globalTags, requireActivity());
                        recyclerView.setAdapter(adapter);
                    });
                }
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Error fetching tags", Toast.LENGTH_SHORT).show());
                throw new RuntimeException(e);
            }
        }).start();
    }
}
