package com.soundpaletteui.Activities.Interactions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Models.Post.CommentModel;
import com.soundpaletteui.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentModel> commentList;

    public CommentAdapter(List<CommentModel> commentList) {
        this.commentList = commentList;
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

        String username = comment.getCommentUsername();
        String commentContent = comment.getCommentText();

        holder.username.setText(username);
        holder.message.setText(commentContent);

        /** ************************** BROKEN ************************** **/
        /** Crashes when opening the ProfileViewFragment                 **/
        /** ************************** BROKEN ************************** **/
        // Open Commenter's profile page
        holder.commenterProfile.setOnClickListener(v -> {
            Log.d("ProfileViewFragment", "Selected to Load Profile User ID# " + username);
            ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(username);

            // Replace the fragment with the User's Profile
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, profileViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() { return commentList.size(); }

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