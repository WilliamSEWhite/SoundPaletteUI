package com.soundpaletteui.Activities.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Adapters.TagBasicAdapter;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Activities.Messages.ChatroomFragment;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.FileClient;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Displays ANOTHER user's profile
public class ProfileViewFragment extends Fragment {

    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private static int viewUserId;
    UserProfileModelLite profile;
    private UserModel viewUser;
    private UserProfileModelLite viewProfile;
    private UserClient userClient;
    private UserClient viewUserClient;
    private static String viewUsername;
    /** Tag stuff */
    private LinearLayoutManager linearLayoutManager;
    private TagClient tagClient;
    private RecyclerView recyclerView;
    private TagBasicAdapter adapter;
    private List<TagModel> tagList;
    private Handler tagScrollHandler;
    private int scrollPosition;
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
    private ImageView imageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private FileClient fileClient;
    private Uri imageUri;
    private Drawable defaultProfileImage;
    private UserClient client;

    public ProfileViewFragment() {
    }


    // New Instance of a ProfileViewFragment with specified UserId
    public static ProfileViewFragment newInstance(int postUserId) {
        ProfileViewFragment fragment = new ProfileViewFragment();
        viewUserId = postUserId;
        Log.d("ProfileViewFragment", "Loading UserId: " + postUserId);
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
        try {
            initComponents(rootView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        UISettings.applyBrightnessGradientBackground(rootView, 50f);
//        framePosts = rootView.findViewById(R.id.frame_posts);
//        gifPosts = rootView.findViewById(R.id.gif_posts);
//        textPosts = rootView.findViewById(R.id.postsToggle);
//        frameTagged = rootView.findViewById(R.id.frame_tagged);
//        gifTagged = rootView.findViewById(R.id.gif_tagged);
//        textTagged = rootView.findViewById(R.id.taggedToggle);
//        followButton = rootView.findViewById(R.id.followButton);
//        messageButton = rootView.findViewById(R.id.messageButton);
//        profileBioDisplay = rootView.findViewById(R.id.profileBio);
//        profileFollowersDisplay = rootView.findViewById(R.id.profileFollowersDisplay);
//        profileFollowingDisplay = rootView.findViewById(R.id.profileFollowingsDisplay);
//        profileUsernameDisplay = rootView.findViewById(R.id.profileUsername);
//
//        // Follow Button Actions
//        followButton.setOnClickListener(v -> {
//            toggleFollow(viewProfile, followButton.isChecked());
//        });
//        // Message Button Actions
//        messageButton.setOnClickListener(v -> {
//            openPrivateMessage();
//        });
//
//        // Post Button Actions
//        framePosts.setOnClickListener(v -> {
//            // Setting the Post Fragment with viewProfile's post algorithm
//            Log.d("ProfileFragment", "Posts selected for User ID# " + viewUserId);
//            replacePostFragment("username", viewUsername);
//
//            try {
//                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
//                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
//                frameTagged.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
////                UISettings.applyBrightnessGradientBackground(rootView, 50f);
//                postsGifDrawable.start();
//                gifHandler.postDelayed(() -> postsGifDrawable.stop(), 800);
//            } catch (ClassCastException e) {
//                e.printStackTrace();
//                Toast.makeText(requireContext(),
//                        "Error casting Posts GIF to GifDrawable",
//                        Toast.LENGTH_SHORT).show();
//            }
//            try {
//                if (gifTagged.getDrawable() instanceof GifDrawable) {
//                    ((GifDrawable) gifTagged.getDrawable()).stop();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            gifPosts.setAlpha(1.0f);
//            gifTagged.setAlpha(0.3f);
//            setButtonTextSelected(textPosts, true);
//            setButtonTextSelected(textTagged, false);
//        });
//
//        // Tags Button Actions
//        frameTagged.setOnClickListener(v -> {
//            // Setting the PostFragment with User's Tagged posts
//            Log.d("ProfileFragment", "Tags selected for User ID# "+viewUserId);
//            replacePostFragment("postusertags", viewUsername);
//
//            try {
//                final GifDrawable savedGifDrawable = (GifDrawable) gifTagged.getDrawable();
//                frameTagged.getBackground().mutate().setAlpha(FULL_ALPHA);
//                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
////                UISettings.applyBrightnessGradientBackground(rootView, 60f);
//                savedGifDrawable.start();
//                gifHandler.postDelayed(() -> savedGifDrawable.stop(), 800);
//            } catch (ClassCastException e) {
//                e.printStackTrace();
//                Toast.makeText(requireContext(),
//                        "Error casting Saved GIF to GifDrawable",
//                        Toast.LENGTH_SHORT).show();
//            }
//            try {
//                if (gifPosts.getDrawable() instanceof GifDrawable) {
//                    ((GifDrawable) gifPosts.getDrawable()).stop();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            gifTagged.setAlpha(1.0f);
//            gifPosts.setAlpha(0.3f);
//            setButtonTextSelected(textTagged, true);
//            setButtonTextSelected(textPosts, false);
//        });
//
//        framePosts.performClick();
        return rootView;
    }

    // Initializes views and loads user data and viewUser
    private void initComponents(View view) throws IOException {
        Log.d("ProfileViewFragment", "viewUserName: " + viewUsername);
        client = SPWebApiRepository.getInstance().getUserClient();
        //user = client.getUserByName(viewUsername);
        client.getUserByName(viewUsername, new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                user = response.body();
                userId = user.getUserId();
                Log.d("ProfileViewFragment", "UserId from body: " + userId);
                loadProfileImage();
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("ProfileViewFragment", "Error retrieving UserModel object");
            }
        });

        fileClient = SPWebApiRepository.getInstance().getFileClient();
        imageView = view.findViewById(R.id.profilePicture);
        defaultProfileImage = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_100);

        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        new GetProfileAsync().execute();

        tagClient = SPWebApiRepository.getInstance().getTagClient();
        tagList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_tag);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        tagScrollHandler = new Handler();

        framePosts = view.findViewById(R.id.frame_posts);
        gifPosts = view.findViewById(R.id.gif_posts);
        textPosts = view.findViewById(R.id.postsToggle);
        frameTagged = view.findViewById(R.id.frame_tagged);
        gifTagged = view.findViewById(R.id.gif_tagged);
        textTagged = view.findViewById(R.id.taggedToggle);
        followButton = view.findViewById(R.id.followButton);
        messageButton = view.findViewById(R.id.messageButton);
        profileBioDisplay = view.findViewById(R.id.profileBio);
        profileFollowersDisplay = view.findViewById(R.id.profileFollowersDisplay);
        profileFollowingDisplay = view.findViewById(R.id.profileFollowingsDisplay);
        profileUsernameDisplay = view.findViewById(R.id.profileUsername);

        // Follow Button Actions
        followButton.setOnClickListener(v -> {
            toggleFollow(viewProfile, followButton.isChecked());
        });
        // Message Button Actions
        messageButton.setOnClickListener(v -> {
            openPrivateMessage();
        });

        // Post Button Actions
        framePosts.setOnClickListener(v -> {
            // Setting the Post Fragment with viewProfile's post algorithm
            Log.d("ProfileFragment", "Posts selected for User ID# " + viewUserId);
            replacePostFragment("username", viewUsername);

            try {
                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameTagged.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
//                UISettings.applyBrightnessGradientBackground(rootView, 50f);
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
            replacePostFragment("postusertags", viewUsername);

            try {
                final GifDrawable savedGifDrawable = (GifDrawable) gifTagged.getDrawable();
                frameTagged.getBackground().mutate().setAlpha(FULL_ALPHA);
                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
//                UISettings.applyBrightnessGradientBackground(rootView, 60f);
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

        //loadProfileImage();
    }

    /** loads the profile image **/
    private void loadProfileImage() {
        Call<FileModel> call = fileClient.getProfileImage(userId);
        ImageUtils.getProfileImage(userId, call, imageView, requireContext());
    }

    // Replaces the Main Screen Fragment in Main Activity
    private void replaceMainFragment(Fragment new_fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, new_fragment, "CHATROOM_FRAGMENT", R.id.mainScreenFrame);
    }

    // Replaces the PostFragment based on the algorithmType and userId
    private void replacePostFragment(String algoType, String userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, userId);
        Navigation.replaceFragment(requireActivity().getSupportFragmentManager(), transaction,
                postFragment, "POST_FRAGMENT", R.id.postFragment);
        /*transaction.replace(R.id.postFragment, postFragment, "POST_FRAGMENT");
        transaction.commit();*/
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
            profile = v;
            profileUsernameDisplay.setText(viewUsername);
            profileBioDisplay.setText(v.getBio());
            profileFollowersDisplay.setText(String.valueOf(v.getFollowerCount()));
            profileFollowingDisplay.setText(String.valueOf(v.getFollowingCount()));
            followButton.setChecked(viewProfile.isFollowing());
            followButton.setText(viewProfile.isFollowing() ? "Unfollow" : "Follow");
            getTags();
        }//end onPostExecute
    }

    // Connect to API Server to perform "Follow" actions
    private void toggleFollow(UserProfileModelLite profile, boolean isFollowing) {
        profile.setIsFollowing(isFollowing);
        new ToggleFollowAsync().execute(profile);

    }

    private class ToggleFollowAsync extends AsyncTask<UserProfileModelLite, Void, Void> {
        protected Void doInBackground(UserProfileModelLite... profiles) {
            UserProfileModelLite profile = profiles[0];
            UserClient client = SPWebApiRepository.getInstance().getUserClient();
            try {
                if (profile.isFollowing()) {
                    client.followUser(viewUsername);
                } else {
                    client.unfollowUser(viewUsername);

                }
                return null;
            } catch (IOException e) {
                Log.e("ToggleFollow", "Error toggling follow", e);
                return null;
            }
        }

        protected void onPostExecute(Void v) {
            new GetProfileAsync().execute();
        }
    }

    private void openPrivateMessage(){
        new OpenPrivateMessageAsync().execute();
    }

    private void openChatroom(ChatroomModelLite chatroom){
        ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatroom.getChatroomId(), chatroom.getName());
        replaceMainFragment(chatroomFragment);
    }

    private class OpenPrivateMessageAsync extends AsyncTask<Void, Void, ChatroomModelLite> {
        protected ChatroomModelLite doInBackground(Void ...v) {
            ChatClient client = SPWebApiRepository.getInstance().getChatClient();
            try {
                ChatroomModelLite chatroom = client.getPrivateChatroom(profile.getUsername());
                return chatroom;
            } catch (IOException e) {
                Log.e("ToggleFollow", "Error toggling follow", e);
                return null;
            }
        }

        protected void onPostExecute(ChatroomModelLite chatroom) {
            openChatroom(chatroom);
        }
    }


    // Retrieves the list of tags from the user profile
    private void getTags() {
        new Thread(() -> {
            List<TagModel> tagList = viewProfile.getUserTags();
            requireActivity().runOnUiThread(() -> {
                if (tagList.size() > 0) {
                    adapter = new TagBasicAdapter((ArrayList<TagModel>) tagList, requireActivity());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setNestedScrollingEnabled(false);

                    // Prevent duplicate fling listener crash
                    if (recyclerView.getOnFlingListener() == null) {
                        SnapHelper snapHelper = new LinearSnapHelper();
                        snapHelper.attachToRecyclerView(recyclerView);
                    }
                }
            });
            startAutoScroll();
        }).start();
    }

    // Auto scrolls the horizontal list of tags
    private void startAutoScroll() {
        scrollPosition = 0;
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

    // Pauses the media player when user leaves the fragment
    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }
}




