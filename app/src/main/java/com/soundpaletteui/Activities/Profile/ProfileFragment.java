package com.soundpaletteui.Activities.Profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.UISettings;
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
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    private TextView profileBio;
    private TextView textPosts;
    private View frameSaved;
    private GifImageView gifSaved;
    private TextView textSaved;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;

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
        userId = String.valueOf(user.getUserId());

        UISettings.applyBrightnessGradientBackground(rootView, 50f);
        framePosts = rootView.findViewById(R.id.frame_posts);
        gifPosts = rootView.findViewById(R.id.gif_posts);
        textPosts = rootView.findViewById(R.id.posts_text);
        frameSaved = rootView.findViewById(R.id.frame_saved);
        gifSaved = rootView.findViewById(R.id.gif_saved);
        textSaved = rootView.findViewById(R.id.saved_text);

        // Assign text for User's Profile Bio
        profileBio = rootView.findViewById(R.id.profile_bio);
        getProfileBio();

        // Edit Profile Button Actions
        AppCompatImageButton buttonEdit = rootView.findViewById(R.id.edit_profile_button);
        buttonEdit.setOnClickListener(v -> {
            ProfileEditFragment profileEditFragment = new ProfileEditFragment();

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainScreenFrame, profileEditFragment)
                    .addToBackStack(null)
                    .commit();
        });

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
                // check if the bio field is empty or null and respond accordingly
                if(userProfile.getBio() == null || userProfile.getBio().trim().isEmpty()) {
                    profileBio.setText("I still need to fill out my bio...");
                }
                else {
                    profileBio.setText(userProfile.getBio());
                }
            });
        }).start();
    }


    // Initializes views and loads user data.
    private void initComponents(View view) {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
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
