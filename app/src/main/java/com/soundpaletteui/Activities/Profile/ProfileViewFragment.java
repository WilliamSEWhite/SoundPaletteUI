package com.soundpaletteui.Activities.Profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Posts.PostAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.ApiClients.PostInteractionClient;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
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
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

// Displays ANOTHER user's profile
public class ProfileViewFragment extends Fragment {

    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private static int viewUserId;
    private UserModel user;
    private UserModel viewUser;
    private UserProfileModelLite viewProfile;
    private UserClient userClient;
    private UserClient viewUserClient;
    private static String viewUsername;
    private TextView profileUsernameDisplay;
    private TextView profileBioDisplay;
    private TextView profileFollowersDisplay;
    private TextView profileFollowingDisplay;
    private CheckBox followButton;
    private Button messageButton;
    private View framePosts;
    private GifImageView gifPosts;
    private TextView textPosts;
    private View frameTagged;
    private GifImageView gifTagged;
    private TextView textTagged;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;

    public ProfileViewFragment() {
    }


    // New Instance of a ProfileViewFragment with specified UserId
    public static ProfileViewFragment newInstance(int postUserId) {
        ProfileViewFragment fragment = new ProfileViewFragment();
        viewUserId = postUserId;
        Log.d("ProfileViewFragment", "Loading Username: " + postUserId);
        return fragment;
    }

    // New Instance of a ProfileViewFragment with specified Username
    public static ProfileViewFragment newInstance(String postUsername) {
        ProfileViewFragment fragment = new ProfileViewFragment();
        viewUsername = postUsername;
        Log.d("ProfileViewFragment", "Loading Username: " + postUsername);
        return fragment;
    }

    // Initializes the fragment with any arguments provided.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Sets te Main Fragment as fragment_profile_view.xml
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_view, container, false);
        initComponents(rootView);

        UISettings.applyBrightnessGradientBackground(rootView, 50f);
        framePosts = rootView.findViewById(R.id.frame_posts);
        gifPosts = rootView.findViewById(R.id.gif_posts);
        textPosts = rootView.findViewById(R.id.postsToggle);
        frameTagged = rootView.findViewById(R.id.frame_tagged);
        gifTagged = rootView.findViewById(R.id.gif_tagged);
        textTagged = rootView.findViewById(R.id.taggedToggle);
        followButton = rootView.findViewById(R.id.followButton);
        messageButton = rootView.findViewById(R.id.messageButton);
        profileBioDisplay = rootView.findViewById(R.id.profileBio);
        profileFollowersDisplay = rootView.findViewById(R.id.profileFollowersDisplay);
        profileFollowingDisplay = rootView.findViewById(R.id.profileFollowingsDisplay);
        profileUsernameDisplay = rootView.findViewById(R.id.profileUsername);

        // Follow Button Actions
        followButton.setChecked(viewProfile.isFollowing());
        followButton.setOnClickListener(v -> {
            toggleFollow(viewProfile, followButton.isChecked());
        });

        followButton.setOnClickListener(v -> {
            Log.d("Follow Button", userId+" wants to follow: "+ viewUserId);
        });

        // Message Button Actions
        messageButton.setOnClickListener(v -> {
            MessageFragment messageFragment = new MessageFragment();
            replaceMainFragment(messageFragment);
        });

        // Post Button Actions
        framePosts.setOnClickListener(v -> {
            // Setting the Post Fragment with viewProfile's post algorithm
            Log.d("ProfileFragment", "Posts selected for User ID# " + viewUserId);
            replacePostFragment("username", String.valueOf(viewUserId));

            try {
                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameTagged.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
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
                if (gifTagged.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifTagged.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifPosts.setAlpha(1.0f);
            gifTagged.setAlpha(0.3f);
            setButtonTextSelected(textPosts, true);
            setButtonTextSelected(textTagged, false);
        });

        // Tags Button Actions
        frameTagged.setOnClickListener(v -> {
            // Setting the PostFragment with User's Tagged posts
            Log.d("ProfileFragment", "Tags selected for User ID# "+viewUserId);
            replacePostFragment("tags", String.valueOf(viewUserId));

            try {
                final GifDrawable savedGifDrawable = (GifDrawable) gifTagged.getDrawable();
                frameTagged.getBackground().mutate().setAlpha(FULL_ALPHA);
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
            gifTagged.setAlpha(1.0f);
            gifPosts.setAlpha(0.3f);
            setButtonTextSelected(textTagged, true);
            setButtonTextSelected(textPosts, false);
        });

        framePosts.performClick();
        return rootView;
    }

    // Initializes views and loads user data and viewUser
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        new GetProfileAsync().execute();
    }

    // Replaces the Main Screen Fragment in Main Activity
    private void replaceMainFragment(Fragment new_fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mainScreenFrame, new_fragment);
        transaction.commit();
    }

    // Replaces the PostFragment based on the algorithmType and userId
    private void replacePostFragment(String algoType, String userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, viewUsername);
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

    // Get a UserProfileModelLite of the user's profile
    private class GetProfileAsync extends AsyncTask<Void,Void, UserProfileModelLite> {
        protected UserProfileModelLite doInBackground(Void... d) {
            System.out.println("UpdateUserInfoAsync");
            try {
                UserClient client = SPWebApiRepository.getInstance().getUserClient();
                viewProfile = client.getUserProfileByUsername(viewUsername);
                return viewProfile;
            } catch (IOException e) {
                Toast.makeText(requireContext(),
                        "Error making post",
                        Toast.LENGTH_SHORT).show();
            }
            return null;
        }//end doInBackground

        // Set the bio, follower count, following count and username
        protected void onPostExecute(UserProfileModelLite v) {
            profileUsernameDisplay.setText(viewUsername);
            profileBioDisplay.setText(v.getBio());
            profileFollowersDisplay.setText(String.valueOf(v.getFollowerCount()));
            profileFollowingDisplay.setText(String.valueOf(v.getFollowingCount()));
        }//end onPostExecute
    }

    // Connect to API Server to perform "Follow" actions
    private void toggleFollow(UserProfileModelLite profile, boolean isFollowing) {
        profile.setIsFollowing(isFollowing);
        new ToggleFollowAsync().execute(profile);
    }

    private class ToggleFollowAsync extends AsyncTask<UserProfileModelLite, Void, UserProfileModelLite> {
        protected UserProfileModelLite doInBackground(UserProfileModelLite... profiles) {
            UserProfileModelLite profile = profiles[0];
            UserClient client = SPWebApiRepository.getInstance().getUserClient();

            try {
                if (profile.isFollowing()) {
                    client.followUser(viewUsername);
                } else {
                    client.unfollowUser(viewUsername);
                }
                return client.getUserProfileByUsername(viewUsername);
            } catch (IOException e) {
                Log.e("ToggleFollow", "Error toggling follow", e);
                return null;
            }
        }

        protected void onPostExecute(UserProfileModelLite updatedProfile) {
            if (updatedProfile != null) {
                viewProfile = updatedProfile;
                profileFollowersDisplay.setText(String.valueOf(viewProfile.getFollowerCount()));
                followButton.setChecked(viewProfile.isFollowing());
            } else {
                Toast.makeText(requireContext(), "Failed to update follow status", Toast.LENGTH_SHORT).show();
            }
        }
    }

}




