package com.soundpaletteui.Activities.Posts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.soundpaletteui.R;
import java.util.List;

/**
 * Displays a list of posts with image, caption, and interactive buttons.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final List<String> imagePaths;
    private final List<String> captions;

    /**
     * Constructs the adapter with image paths and captions.
     */
    public PostAdapter(List<String> imagePaths, List<String> captions) {
        this.imagePaths = imagePaths;
        this.captions = captions;
    }

    /**
     * Creates and inflates the ViewHolder for a post.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_posts, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data (image and caption) to a ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(holder.postImage.getContext()).load(imagePath).into(holder.postImage);
        if (captions != null && position < captions.size()) {
            holder.postCaption.setText(captions.get(position));
        } else {
            holder.postCaption.setText("No caption available");
        }
        holder.likeButton.setOnClickListener(v -> {
            if (holder.isLiked) {
                holder.likeButton.setImageResource(R.drawable.post_fav_empty_24);
            } else {
                holder.likeButton.setImageResource(R.drawable.post_fav_filled_24);
            }
            holder.isLiked = !holder.isLiked;
        });
        holder.commentButton.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Commented on Post", Toast.LENGTH_SHORT).show()
        );
        holder.saveButton.setOnClickListener(v -> {
            if (holder.isSaved) {
                holder.saveButton.setImageResource(R.drawable.post_saved_empty_24);
            } else {
                holder.saveButton.setImageResource(R.drawable.post_saved_filled_24);
            }
            holder.isSaved = !holder.isSaved;
        });
    }

    /**
     * Returns the total number of posts in the list.
     */
    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    /**
     * Holds references to each post's UI components.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postCaption;
        ImageButton likeButton, commentButton, saveButton;
        boolean isLiked = false;
        boolean isSaved = false;

        /**
         * Constructs the ViewHolder and finds its views.
         */
        ViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
            postCaption = itemView.findViewById(R.id.postCaption);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            saveButton = itemView.findViewById(R.id.postSaveButton);
        }
    }
}
