package com.soundpaletteui.Activities.Trending;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Messages.ChatroomFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.ApiClients.ChatClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ProfileViewHolder> {

    private final ArrayList<UserProfileModelLite> profileList;
    private Context context;

    public SearchProfileAdapter(ArrayList<UserProfileModelLite> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_search_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        UserProfileModelLite userView = profileList.get(position);
        final String username = userView.getUsername();
        final int followers = userView.getFollowerCount();

        holder.usernameDisplay.setText(username);
        holder.followerDisplay.setText(String.valueOf(followers));
        holder.followButton.setChecked(userView.isFollowing());

        // Profile Button
        holder.profileButton.setOnClickListener(v -> {
            ProfileViewFragment profileFragment = ProfileViewFragment.newInstance(username);
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, profileFragment, "ProfileViewFragment", R.id.mainScreenFrame);
        });

        // Follow/Unfollow Toggle
        holder.followButton.setOnClickListener(v -> {
            boolean isCurrentlyFollowing = userView.isFollowing();
            new ToggleFollowAsync(userView, !isCurrentlyFollowing).execute();
        });

        // Message Button
        holder.messageButton.setOnClickListener(v -> {
            new OpenPrivateMessageAsync(username).execute();
        });
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView usernameDisplay, followerDisplay;
        Button profileButton, messageButton;
        CheckBox followButton;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameDisplay = itemView.findViewById(R.id.usernameDisplay);
            followerDisplay = itemView.findViewById(R.id.followerDisplay);
            profileButton = itemView.findViewById(R.id.profileButton);
            followButton = itemView.findViewById(R.id.followButton);
            messageButton = itemView.findViewById(R.id.messageButton);
        }
    }

    // Replace current fragment on screen
    private void replaceMainFragment(Fragment newFragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, newFragment, "CHATROOM_FRAGMENT", R.id.mainScreenFrame);
    }

    // AsyncTask for toggling follow/unfollow
    private class ToggleFollowAsync extends AsyncTask<Void, Void, Boolean> {
        private final UserProfileModelLite profile;
        private final boolean shouldFollow;

        public ToggleFollowAsync(UserProfileModelLite profile, boolean shouldFollow) {
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
                notifyDataSetChanged(); // Refresh UI
            }
        }
    }

    // AsyncTask to open private message chatroom
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

    private void openChatroom(ChatroomModelLite chatroom) {
        ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatroom.getChatroomId(), chatroom.getName());
        replaceMainFragment(chatroomFragment);
    }
}
