package com.soundpaletteui.Activities.Profile;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Infrastructure.Adapters.TagSelectAdapter;
import com.soundpaletteui.Infrastructure.Adapters.UserTagAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

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
        // get the nav id to exit this fragment to the previous one
        if(getArguments() != null) {
            fragId = getArguments().getInt("nav", 0);
        }
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        tagClient = SPWebApiRepository.getInstance().getTagClient();;

        recyclerView = view.findViewById(R.id.userTagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnDone = view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> done());

        List<TagModel> globalTags = new ArrayList<>();
        List<TagModel> userTags = new ArrayList<>();

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
                Response<ResponseBody> response = tagClient.updateTags(user.getUserId(), selectedTags);
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(activity, "Tags saved successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Failed to save tags",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                Toast.makeText(requireActivity(), "Error saving tags",
                        Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }).start();

        Bundle args = new Bundle();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch(fragId) {
            case 0:     // return to profileFragment
                Navigation.replaceFragment(fragmentManager, new ProfileFragment(),
                        "PROFILE_FRAGMENT", R.id.mainScreenFrame);
                /*
                ProfileFragment profileFragment = new ProfileFragment();

                transaction.addToBackStack(null);
                transaction.replace(R.id.mainScreenFrame, profileFragment);
                transaction.commit();*/
                break;
            case 1:     // return to profileEditFragment
                args.putParcelableArrayList("selectedTags", new ArrayList<>(selectedTags));
                Navigation.replaceFragment(fragmentManager, new ProfileEditFragment(),
                        "PROFILE_EDIT_FRAGMENT", R.id.mainScreenFrame);
                /*ProfileEditFragment profileEditFragment = new ProfileEditFragment();
                profileEditFragment.setArguments(args);

                transaction.addToBackStack(null);
                transaction.replace(R.id.mainScreenFrame, profileEditFragment);
                transaction.commit();*/
                break;
        }
    }

    /** retrieve user tags and mark selected */
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
                        //globalTags = tags;
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