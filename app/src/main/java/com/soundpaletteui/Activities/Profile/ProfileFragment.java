package com.soundpaletteui.Activities.Profile;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Notifications.NotificationFragment;
import com.soundpaletteui.Infrastructure.Adapters.TagBasicAdapter;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;
import com.soundpaletteui.SPApiServices.ApiClients.FileClient;
import com.soundpaletteui.SPApiServices.ApiClients.NotificationClient;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;

import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Infrastructure.Models.TagModel;

import com.soundpaletteui.Infrastructure.Models.User.UserProfileModel;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.LinearSnapHelper;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

// Displays and manages a user's profile, including viewing their posts, saved items, and tags.
// Handles profile information updates, dark mode settings, and navigation to notifications and edit screens.
public class ProfileFragment extends Fragment {
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private String userId;
    private UserModel user;
    private UserClient userClient;
    private View framePosts, frameSaved, notificationDot;
    private GifImageView gifPosts, gifSaved;
    private TextView usernameDisplay, profileBio, textPosts, textSaved;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;
    private Button viewNotificationButton, btnEditSaved;
    private LinearLayoutManager linearLayoutManager;
    private TagClient tagClient;
    private RecyclerView recyclerView;
    private TagBasicAdapter adapter;
    private List<TagModel> tagList;
    private Handler tagScrollHandler;
    private int scrollPosition;
    private TextView profileFollowersDisplay, profileFollowingDisplay;
    private FileClient fileClient;
    private ImageView imageView;
    private boolean darkMode;
    private String selectedTab = "posts";
    EmojiBackgroundView emojiBackground;


    private SharedPreferences.OnSharedPreferenceChangeListener darkModeListener =
            (sharedPreferences, key) -> {
                if ("dark_mode_enabled".equals(key)) {
                    updateUI();
                }
            };

    public ProfileFragment() {
    }

    // Turn the notification indicator ON/OFF
    public void setNotificationDotVisible(boolean visible) {
        if (notificationDot != null) {
            notificationDot.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register dark mode listener
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(darkModeListener);
        loadProfileImage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.unregisterOnSharedPreferenceChangeListener(darkModeListener);
    }

    // Refreshes profile data when resuming fragment
    @Override
    public void onResume() {
        super.onResume();
        user = AppSettings.getInstance().getUser();
        updateUI();
        new Handler(Looper.getMainLooper()).postDelayed(() -> getProfileBio(), 500);
        loadProfileImage();

        if (user != null) {
            userId = String.valueOf(user.getUserId());
            ((MainScreenActivity) requireActivity()).startNotificationPolling(Integer.parseInt(userId));
        }
    }


    // Inflates the layout and sets up UI for posts and saved content.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents(rootView);
        return rootView;
    }

    // Updates the UI background based on the current dark mode setting and selected tab.
    private void updateUI() {
        View rootLayout = getView().findViewById(R.id.root_layout);
        darkMode = DarkModePreferences.isDarkModeEnabled(getContext());
        if ("posts".equals(selectedTab)) {
            UISettings.applyBrightnessGradientBackground(rootLayout, 40, darkMode);
        } else {
            UISettings.applyBrightnessGradientBackground(rootLayout, 50f, darkMode);
        }
    }

    // Initializes views and loads user data.
    private void initComponents(View rootView) {
        final View rootLayout = rootView.findViewById(R.id.root_layout);
        darkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootLayout, 40f, darkMode);
        emojiBackground = rootView.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);

        notificationDot = rootView.findViewById(R.id.notificationDot);

        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();

        tagClient = SPWebApiRepository.getInstance().getTagClient();
        tagList = new ArrayList<>();

        viewNotificationButton = rootView.findViewById(R.id.viewNotificationButton);
        btnEditSaved = rootView.findViewById(R.id.editSavedButton);

        viewNotificationButton.setOnClickListener(v -> viewNotifications(new NotificationFragment(), "VIEW_NOTIFICATIONS_FRAGMENT"));
        btnEditSaved.setOnClickListener(v -> editSaved(new ProfileEditFragment(), "PROFILE_EDIT_FRAGMENT"));

        recyclerView = rootView.findViewById(R.id.recycler_tag);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        tagScrollHandler = new Handler();

        // Check if user profile is loaded
        if (user != null) {
            userId = String.valueOf(user.getUserId());
        } else {
            Toast.makeText(requireContext(), "User profile not loaded.", Toast.LENGTH_SHORT).show();
        }

        framePosts = rootView.findViewById(R.id.frame_posts);
        gifPosts = rootView.findViewById(R.id.gif_posts);
        textPosts = rootView.findViewById(R.id.postsToggle);
        frameSaved = rootView.findViewById(R.id.frame_saved);
        gifSaved = rootView.findViewById(R.id.gif_saved);
        textSaved = rootView.findViewById(R.id.savedToggle);

        imageView = rootView.findViewById(R.id.profilePicture);

        // Assign username
        usernameDisplay = rootView.findViewById(R.id.profileUsername);
        usernameDisplay.setText(user.getUsername());

        // Assign text for User's Profile Bio and follower/following counts
        profileBio = rootView.findViewById(R.id.profileBio);
        profileFollowersDisplay = rootView.findViewById(R.id.profileFollowersDisplay);
        profileFollowingDisplay = rootView.findViewById(R.id.profileFollowingsDisplay);

        // Post Button Actions
        framePosts.setOnClickListener(v -> {
            emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);

            selectedTab = "posts";
            try {
                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameSaved.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
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
            replaceFragment("user", String.valueOf(userId), true);
            UISettings.applyBrightnessGradientBackground(rootLayout, 40f, darkMode);
        });

        // Saved Button Actions
        frameSaved.setOnClickListener(v -> {
            emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);

            selectedTab = "saved";
            try {
                final GifDrawable savedGifDrawable = (GifDrawable) gifSaved.getDrawable();
                frameSaved.getBackground().mutate().setAlpha(FULL_ALPHA);
                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
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

            Log.d("ProfileFragment", "Saved clicked for User ID# " + userId);
            replaceFragment("saved", userId, false);
            // Re-apply the UI background for saved
            UISettings.applyBrightnessGradientBackground(rootLayout, 50f, darkMode);
        });

        // Default view is the "posts" tab
        framePosts.performClick();
        getProfileBio();
        loadProfileImage();
        getTags();
    }

    // Loads the profile image
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

    // Calls function to get user bio text
    private void getProfileBio() {
        getUserBio();
    }

    // Populates the user bio field
    private void getUserBio() {
        new Thread(() -> {
            UserProfileModel userProfile;
            try {
                userProfile = userClient.getUserProfile(Integer.parseInt(userId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Update UI on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if(userProfile == null) {
                    profileBio.setText("Please fill in your bio...");
                    return;
                }
                // Check if the bio field is empty or null and respond accordingly
                if(userProfile.getBio() == null || userProfile.getBio().trim().isEmpty()) {
                    profileBio.setText("I still need to fill out my bio...");
                } else {
                    profileBio.setText(userProfile.getBio());
                    profileFollowersDisplay.setText(String.valueOf(userProfile.getFollowerCount()));
                    profileFollowingDisplay.setText(String.valueOf(userProfile.getFollowingCount()));
                }
            });
        }).start();
    }

    // Opens the Edit Saved screen
    private void editSaved(Fragment newFragment, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    // Edit user tags
    private void viewNotifications(Fragment newFragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("nav", 0);
        newFragment.setArguments(bundle);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, tag, R.id.mainScreenFrame);
    }

    // Auto scrolls the horizontal list of tags
    private void startAutoScroll() {
        tagScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scrollPosition < adapter.getItemCount()) {
                    recyclerView.smoothScrollToPosition(scrollPosition++);
                } else {
                    scrollPosition = 0;
                    recyclerView.smoothScrollToPosition(scrollPosition);
                }
                tagScrollHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    // Refreshes the user tag list in the recycler view
    private void refreshTagList() {
        if (getArguments() != null && getArguments().containsKey("selectedTags")) {
            ArrayList<TagModel> selectedTags = getArguments().getParcelableArrayList("selectedTags");

            if (selectedTags != null && !selectedTags.isEmpty()) {
                adapter = new TagBasicAdapter(selectedTags, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("ProfileEditFragment", "No tags received or empty list");
            }
        }
    }

    // Retrieves the list of tags from the user profile
    private void getTags() {
        new Thread(() -> {
            try {
                List<TagModel> tags = tagClient.getUserTags(user.getUserId());
                requireActivity().runOnUiThread(() -> {
                    tagList = tags;
                    adapter = new TagBasicAdapter((ArrayList<TagModel>) tagList, requireActivity());
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
    private void replaceFragment(String algoType, String userId, boolean showEditButton) {
        Log.d("SHOW EDIT BUTTON", String.valueOf(showEditButton));
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, userId, showEditButton);
        Navigation.replaceFragment(requireActivity().getSupportFragmentManager(),
                transaction, postFragment,
                "POST_FRAGMENT",
                R.id.postFragment);
    }

    // Sets the style of a TextView to selected or unselected.
    private void setButtonTextSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setTextSize(20);
        } else {
            textView.setTextSize(18);
        }
    }

    @Override
    //Releases MediaPlayer resources when the fragment is paused
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }
}
