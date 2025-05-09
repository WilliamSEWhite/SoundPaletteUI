package com.soundpaletteui.Activities.Interactions;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Models.Post.CommentModel;
import com.soundpaletteui.R;

import java.util.List;

// Individual comments that get displayed in the CommentBottomSheet
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentModel> commentList;
    private final FragmentActivity activity;
    private final OnProfileClickListener profileClickListener;

    public CommentAdapter(FragmentActivity activity, List<CommentModel> commentList, OnProfileClickListener listener) {
        this.activity = activity;
        this.commentList = commentList;
        this.profileClickListener = listener;
    }

    // Requires a list of CommentModel
    public CommentAdapter(List<CommentModel> commentList, FragmentActivity activity) {
        this.commentList = commentList;
        this.activity = activity;
        this.profileClickListener = null;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);
        // Get the username and comment content from the CommentModel
        String username = comment.getCommentUsername();
        String commentContent = comment.getCommentText();

        // Set the username and message in the CommentAdapter
        holder.username.setText(username);
        holder.message.setText(commentContent);

        // ImageButton listener for Profile Picture
        // Redirects the user to the commenter's profile page
        holder.commenterProfile.setOnClickListener(v -> {
            Log.d("ProfileViewFragment", "Username clicked: " + username);

            if (profileClickListener != null) {
                profileClickListener.onProfileClick(); // triggers dismiss()
            }

            // Delay navigation to ensure BottomSheet is dismissed first
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(username);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                com.soundpaletteui.Infrastructure.Utilities.Navigation.replaceFragment(
                        fragmentManager, profileViewFragment, "PROFILE_VIEW_FRAGMENT", R.id.mainScreenFrame
                );
            }, 300);
        });
    }

    public interface OnProfileClickListener {
        void onProfileClick();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // Get the views for the CommentAdapter
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageButton commenterProfile;
        TextView username, message;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commenterProfile = itemView.findViewById(R.id.commenterProfile);
            username = itemView.findViewById(R.id.adapter_comment_user);
            message = itemView.findViewById(R.id.adapter_comment_msg);
        }
    }
}
