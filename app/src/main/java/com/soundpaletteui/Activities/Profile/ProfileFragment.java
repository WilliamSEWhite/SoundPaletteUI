package com.soundpaletteui.Activities.Profile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Adapters.UserTagAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

// Displays and manages a user's profile, including posts and saved content.
public class ProfileFragment extends Fragment {

    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private String userId;
    private UserModel user;
    private UserClient userClient;
    private View framePosts;
    private GifImageView gifPosts;
    private TextView usernameDisplay;
    private TextView profileBio;
    private TextView textPosts;
    private View frameSaved;
    private GifImageView gifSaved;
    private TextView textSaved;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;
    /** buttons */
    Button btnEditTags, btnEditSaved;
    /** Tag stuff */
    private LinearLayoutManager linearLayoutManager;
    private TagClient tagClient;
    private RecyclerView recyclerView;
    private UserTagAdapter adapter;
    private List<TagModel> tagList;
    private Handler tagScrollHandler;
    private  int scrollPosition;
    private TextView profileFollowersDisplay;
    private TextView profileFollowingDisplay;


    public ProfileFragment() {
    }

    // Initializes the fragment with any arguments provided.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** refreshes profile data when resuming fragment */
    @Override
    public void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(() -> getProfileBio(), 500);
    }

    // Inflates the layout and sets up UI for posts and saved content.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponents(rootView);

        // check if user profile is loaded
        if(user != null) {
            userId = String.valueOf(user.getUserId());
        }
        else {
            Toast.makeText(requireContext(), "User profile not loaded.", Toast.LENGTH_SHORT).show();
        }

        UISettings.applyBrightnessGradientBackground(rootView, 50f);
        framePosts = rootView.findViewById(R.id.frame_posts);
        gifPosts = rootView.findViewById(R.id.gif_posts);
        textPosts = rootView.findViewById(R.id.postsToggle);
        frameSaved = rootView.findViewById(R.id.frame_saved);
        gifSaved = rootView.findViewById(R.id.gif_saved);
        textSaved = rootView.findViewById(R.id.savedToggle);

        // Assign username
        usernameDisplay = rootView.findViewById(R.id.profileUsername);
        usernameDisplay.setText(user.getUsername());

        // Assign text for User's Profile Bio
        profileBio = rootView.findViewById(R.id.profileBio);
        profileFollowersDisplay = rootView.findViewById(R.id.profileFollowersDisplay);

        // Assign number of following
        profileFollowingDisplay = rootView.findViewById(R.id.profileFollowingsDisplay);



        getProfileBio();

        // Edit Profile Button Actions
        AppCompatImageButton buttonEdit = rootView.findViewById(R.id.editProfileButton);
        buttonEdit.setOnClickListener(v -> editProfile(new ProfileEditFragment(), "PROFILE_EDIT_FRAGMENT"));

        // Post Button Actions
        framePosts.setOnClickListener(v -> {
            try {
                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameSaved.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                UISettings.applyBrightnessGradientBackground(rootView, 50f);
                postsGifDrawable.start();
                gifHandler.postDelayed(() -> postsGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Posts GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifSaved.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifSaved.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifPosts.setAlpha(1.0f);
            gifSaved.setAlpha(0.3f);
            setButtonTextSelected(textPosts, true);
            setButtonTextSelected(textSaved, false);

            Log.d("ProfileFragment", "Posts selected for User ID# " + userId);
            replaceFragment("user", String.valueOf(userId));
        });

        // Saved Button Actions
        frameSaved.setOnClickListener(v -> {
            try {
                final GifDrawable savedGifDrawable = (GifDrawable) gifSaved.getDrawable();
                frameSaved.getBackground().mutate().setAlpha(FULL_ALPHA);
                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                UISettings.applyBrightnessGradientBackground(rootView, 60f);
                savedGifDrawable.start();
                gifHandler.postDelayed(() -> savedGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Saved GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifPosts.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifPosts.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifSaved.setAlpha(1.0f);
            gifPosts.setAlpha(0.3f);
            setButtonTextSelected(textSaved, true);
            setButtonTextSelected(textPosts, false);

            Log.d("ProfileFragment", "Saved clicked for User ID# "+userId);
            replaceFragment("saved", userId);
        });

        framePosts.performClick();
        return rootView;
    }

    /** Initializes views and loads user data. */
    private void initComponents(View view) {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();

        tagClient = SPWebApiRepository.getInstance().getTagClient();
        tagList = new ArrayList<>();

        btnEditTags = view.findViewById(R.id.editTagsButton);
        btnEditSaved = view.findViewById(R.id.editSavedButton);

        btnEditTags.setOnClickListener(v -> editUserTags(new ProfileEditTagsFragment(), "PROFILE_EDIT_TAGS_FRAGMENT"));
        btnEditSaved.setOnClickListener(v -> editSaved(new ProfileEditFragment(), "PROFILE_EDIT_FRAGMENT"));

        recyclerView = view.findViewById(R.id.recycler_tag);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        tagScrollHandler = new Handler();

        getTags();
    }

    /** move to edit profile fragment */
    private void editProfile(Fragment newFragment, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    private void getProfileBio() {
        getUserBio();
    }

    /** populates the user bio field */
    private void getUserBio() {
        new Thread(() -> {
            UserProfileModel userProfile;
            try {
                userProfile = userClient.getUserProfile(Integer.parseInt(userId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // update UI on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if(userProfile == null) {
                    profileBio.setText("Please fill in your bio...");
                    return;
                }
                // check if the bio field is empty or null and respond accordingly
                if(userProfile.getBio() == null || userProfile.getBio().trim().isEmpty()) {
                    profileBio.setText("I still need to fill out my bio...");
                }
                else {
                    profileBio.setText(userProfile.getBio());
                    profileFollowersDisplay.setText(String.valueOf(userProfile.getFollowerCount()));
                    profileFollowingDisplay.setText(String.valueOf(userProfile.getFollowingCount()));

                }
            });
        }).start();
    }

    private void editSaved(Fragment newFragment, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    /** edit user tags */
    private void editUserTags(Fragment newFragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("nav", 0);
        newFragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    /** auto scrolls the horizontal list of tags */
    private void startAutoScroll() {
        tagScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(scrollPosition < adapter.getItemCount()) {
                    recyclerView.smoothScrollToPosition(scrollPosition++);
                }
                else {
                    scrollPosition = 0;
                    recyclerView.smoothScrollToPosition(scrollPosition);
                }
                tagScrollHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    /** refreshes the user tag list in the recycler view */
    private void refreshTagList() {
        if (getArguments() != null && getArguments().containsKey("selectedTags")) {
            ArrayList<TagModel> selectedTags = getArguments().getParcelableArrayList("selectedTags");

            if (selectedTags != null && !selectedTags.isEmpty()) {
                // Update the RecyclerView with the new tag list
                adapter = new UserTagAdapter(selectedTags, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                Log.d("ProfileEditFragment", "No tags received or empty list");
            }
        }
    }

    /** retrieves the list of tags from the user profile */
    private void getTags() {
        new Thread(() -> {
            try {
                List<TagModel> tags = tagClient.getUserTags(user.getUserId());
                //List<TagModel> tags = tagClient.getTags();
                requireActivity().runOnUiThread(() -> {
                    tagList = tags;
                    adapter = new UserTagAdapter((ArrayList<TagModel>) tagList, requireActivity());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    SnapHelper snapHelper = new LinearSnapHelper();
                    snapHelper.attachToRecyclerView(recyclerView);
                });
                startAutoScroll();
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Error fetching tags", Toast.LENGTH_SHORT).show());
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Replaces the PostFragment based on the algorithmType and userId
    private void replaceFragment(String algoType, String userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, userId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    // Sets the style of a TextView to selected or unselected.
    private void setButtonTextSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(19);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextSize(18);
        }
    }
}
