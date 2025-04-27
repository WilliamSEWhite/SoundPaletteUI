package com.soundpaletteui.Activities.Trending;

import android.content.Context;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.soundpaletteui.Activities.Messages.ChatroomFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;

// Adapter for displaying a list of user profiles with options to view, follow/unfollow, or send a message.
public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ProfileViewHolder> {
    // List of profiles to display
    private final ArrayList<UserSearchModel> profileList;

    // Context for fragment navigation
    private Context context;

    // Constructor for setting up the adapter
    public SearchProfileAdapter(ArrayList<UserSearchModel> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    @Override
    // Creates a new ViewHolder for a user profile
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_search_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    // Binds the user's profile information to the ViewHolder
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        UserSearchModel userView = profileList.get(position);
        final String username = userView.getUsername();
        final int followers = userView.getFollowerCount();

        // Set username and follower count
        holder.usernameDisplay.setText(username);
        holder.followerDisplay.setText(String.valueOf(followers));
        holder.followButton.setChecked(userView.isFollowing());

        // Open profile when profile button is clicked
        holder.profileButton.setOnClickListener(v -> {
            ProfileViewFragment profileFragment = ProfileViewFragment.newInstance(username);
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, profileFragment, "ProfileViewFragment", R.id.mainScreenFrame);
        });

        // Set button text based on following state
        holder.followButton.setText(userView.isFollowing() ? "Unfollow" : "Follow");

        // Toggle follow/unfollow when button is clicked
        holder.followButton.setOnClickListener(v -> {
            boolean isCurrentlyFollowing = userView.isFollowing();
            new ToggleFollowAsync(userView, !isCurrentlyFollowing).execute();
        });

        // Open message screen when message button is clicked
        holder.messageButton.setOnClickListener(v -> {
            new OpenPrivateMessageAsync(username).execute();
        });

        // Load profile image
        new Thread(() -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                ImageUtils.getProfileImageByUsername(username, holder.imageView, context);
            });
        }).start();
    }

    @Override
    // Returns the number of profiles
    public int getItemCount() {
        return profileList.size();
    }

    // ViewHolder class to hold the view for each user profile
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView usernameDisplay, followerDisplay;
        Button profileButton, messageButton;
        CheckBox followButton;
        ImageView imageView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            // Link views to layout items
            usernameDisplay = itemView.findViewById(R.id.usernameDisplay);
            followerDisplay = itemView.findViewById(R.id.followerDisplay);
            profileButton = itemView.findViewById(R.id.profileButton);
            followButton = itemView.findViewById(R.id.followButton);
            messageButton = itemView.findViewById(R.id.messageButton);
            imageView = itemView.findViewById(R.id.profilePicture);
        }
    }

    // Replaces the main fragment with the given one
    private void replaceMainFragment(Fragment newFragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, "CHATROOM_FRAGMENT", R.id.mainScreenFrame);
    }

    // AsyncTask for toggling follow/unfollow status
    private class ToggleFollowAsync extends AsyncTask<Void, Void, Boolean> {
        private final UserSearchModel profile;
        private final boolean shouldFollow;

        public ToggleFollowAsync(UserSearchModel profile, boolean shouldFollow) {
            this.profile = profile;
            this.shouldFollow = shouldFollow;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            UserClient client = SPWebApiRepository.getInstance().getUserClient();
            try {
                if (shouldFollow) {
                    client.followUser(profile.getUsername());
                } else {
                    client.unfollowUser(profile.getUsername());
                }
                return true;
            } catch (IOException e) {
                Log.e("ToggleFollow", "Error toggling follow", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                profile.setIsFollowing(shouldFollow);
                notifyDataSetChanged(); // Refresh the UI to reflect follow state
            }
        }
    }

    // AsyncTask to open or create a private message chatroom
    private class OpenPrivateMessageAsync extends AsyncTask<Void, Void, ChatroomModelLite> {
        private final String username;

        public OpenPrivateMessageAsync(String username) {
            this.username = username;
        }

        @Override
        protected ChatroomModelLite doInBackground(Void... voids) {
            ChatClient client = SPWebApiRepository.getInstance().getChatClient();
            try {
                return client.getPrivateChatroom(username);
            } catch (IOException e) {
                Log.e("OpenPrivateMessage", "Error getting chatroom", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ChatroomModelLite chatroom) {
            if (chatroom != null) {
                openChatroom(chatroom);
            }
        }
    }

    // Opens the chatroom fragment
    private void openChatroom(ChatroomModelLite chatroom) {
        ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatroom.getChatroomId(), chatroom.getName());
        replaceMainFragment(chatroomFragment);
    }
}
