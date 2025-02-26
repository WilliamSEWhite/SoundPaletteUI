package com.soundpaletteui.Activities.Posts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Interactions.CommentBottomSheet;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.Models.PostContentModel;
import com.soundpaletteui.R;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostModel> postList;
    private Context context;
    private static final int TEXT_POST = 1;
    private static final int AUDIO_POST = 2;
    private static final int IMAGE_POST = 3;

    public PostAdapter(List<PostModel> postList) {
        this.postList = postList;
    }

    // Create the main View using adapter_posts.xml
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_posts, parent, false);
        return new PostViewHolder(view);
    }

    // Binds (sets) the objects in adapter_posts.xml
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostModel post = postList.get(position);
        int postId = post.getPostId();
        holder.postUsername.setText(post.getUsername());        // Get username from Post
        holder.postCaption.setText(post.getPostCaption());      // Get caption from Post
        holder.postFragmentDisplay.removeAllViews();            // Clear previous content
        LayoutInflater inflater = LayoutInflater.from(context);
        View fragmentView;

        // Sets the fragmentView in adapter_posts based on the postType
        if (post.getPostType() == TEXT_POST) {                  // Text Post
            fragmentView = inflater.inflate(R.layout.adapter_posts_text, holder.postFragmentDisplay, false);
            TextView postTextDisplay = fragmentView.findViewById(R.id.postTextDisplay);
            postTextDisplay.setText(post.getPostContent().getPostContent());
        } else if (post.getPostType() == AUDIO_POST) {          //Audio Post
            fragmentView = new View(context);
        } else if (post.getPostType() == IMAGE_POST) {          // Image Post
            fragmentView = inflater.inflate(R.layout.adapter_posts_image, holder.postFragmentDisplay, false);
            ImageView postImageDisplay = fragmentView.findViewById(R.id.postImageDisplay);
            String imageName = post.getPostContent().getPostContent().replace(".png", "").replace(".jpg", "");
            int imageResource = holder.itemView.getContext().getResources().getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
            postImageDisplay.setImageResource(imageResource);
        } else {                                                // Empty View
            fragmentView = new View(context);
        }

        // Like Button Actions
        holder.likeButton.setOnClickListener(v -> {
            if (holder.isLiked) {
                holder.likeButton.setImageResource(R.drawable.post_fav_empty_24);
                Log.d("Like Button", "Unliked Post ID#" +postId);
            } else {
                holder.likeButton.setImageResource(R.drawable.post_fav_filled_24);
                Log.d("Like Button", "Liked Post ID#" +postId);
            }
            holder.isLiked = !holder.isLiked;
        });

        // Comment Button Actions
        holder.commentButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CommentBottomSheet.class);
            intent.putExtra("postId", position); // Use position as a placeholder postId
            v.getContext().startActivity(intent);
        });

        // Save Button Actions
        holder.saveButton.setOnClickListener(v -> {
            if (holder.isSaved) {
                holder.saveButton.setImageResource(R.drawable.post_saved_empty_24);
                Log.d("Save Button", "Unsaved Post ID#" +postId);
            } else {
                holder.saveButton.setImageResource(R.drawable.post_saved_filled_24);
                Log.d("Save Button", "Saved Post ID#" +postId);
            }
            holder.isSaved = !holder.isSaved;
        });

        holder.postFragmentDisplay.addView(fragmentView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Set and find all objects in adapter_posts.xml
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postUsername, postCaption;
        ViewGroup postFragmentDisplay;
        ImageButton likeButton, commentButton, saveButton;
        boolean isLiked = false;
        boolean isSaved = false;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postUsername = itemView.findViewById(R.id.postUsername);
            postCaption = itemView.findViewById(R.id.postCaption);
            postFragmentDisplay = itemView.findViewById(R.id.postFragmentDisplay);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            saveButton = itemView.findViewById(R.id.postSaveButton);
        }
    }
}