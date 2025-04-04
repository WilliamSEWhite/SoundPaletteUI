package com.soundpaletteui.Activities.Profile;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Adapters.TagSelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.UserTagAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileEditTagsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TagSelectAdapter adapter;
    private List<TagModel> globalTags;
    private List<TagModel> userTags;
    private TagClient tagClient;
    private UserModel user;
    private UserClient userClient;
    private int userId;
    private Button btnDone;
    private int fragId;     // from which fragment did I come from?

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user_tags, container, false);
        if(getArguments() != null) {
            fragId = getArguments().getInt("nav", 0);
        }
        // Apply dark mode gradient background
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(view.getContext());
        UISettings.applyBrightnessGradientBackground(view, 50f, isDarkMode);

        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        tagClient = SPWebApiRepository.getInstance().getTagClient();

        recyclerView = view.findViewById(R.id.userTagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnDone = view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> done());

        globalTags = new ArrayList<>();
        userTags = new ArrayList<>();

        getTags();
    }

    private void done() {
        if (globalTags == null || globalTags.isEmpty()) {
            Toast.makeText(requireActivity(), "No tags were selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if(adapter == null) {
            Toast.makeText(requireActivity(), "No tags were selected", Toast.LENGTH_SHORT).show();
            return;
        }
        List<TagModel> selectedTags = new ArrayList<>();
        for(TagModel tag : globalTags) {
            if(tag.isSelected()) {
                selectedTags.add(tag);
            }
        }
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
