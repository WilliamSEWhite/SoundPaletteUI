package com.soundpaletteui.Activities.Posts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Interactions.CommentBottomSheet;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.ApiClients.PostInteractionClient;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.R;

import java.io.IOException;
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

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_posts, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostModel post = postList.get(position);
        int postId = post.getPostId();
        int postUserId = 1; // Placeholder: Replace with actual user ID if available
        String postUsername = post.getUsername();

        holder.postUsername.setText(postUsername);
        holder.postCaption.setText(post.getPostCaption());
        holder.postFragmentDisplay.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);
        View fragmentView;

        if (post.getPostType() == TEXT_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_text, holder.postFragmentDisplay, false);
            TextView postTextDisplay = fragmentView.findViewById(R.id.postTextDisplay);
            postTextDisplay.setText(post.getPostContent().getPostTextContent());
        } else if (post.getPostType() == IMAGE_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_image, holder.postFragmentDisplay, false);
            ImageView postImageDisplay = fragmentView.findViewById(R.id.postImageDisplay);
            String imageName = post.getPostContent().getPostTextContent().replace(".png", "").replace(".jpg", "");
            int imageResource = holder.itemView.getContext().getResources().getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
            postImageDisplay.setImageResource(imageResource);
        } else {
            fragmentView = new View(context);
        }
/*
        // Open Poster's profile page
        holder.postersProfile.setOnClickListener(v -> {
            ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(postUserId);
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, profileViewFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
*/
        CheckBox postIsLiked = holder.likeButton;
        postIsLiked.setChecked(post.getIsLiked());

        postIsLiked.setOnClickListener(v -> toggleLike(post, postIsLiked.isChecked()));

        // Like Button Actions
//        holder.likeButton.setOnClickListener(v -> {
//            holder.isLiked = !holder.isLiked;
//            holder.likeButton.setImageResource(holder.isLiked ? R.drawable.post_fav_filled_24 : R.drawable.post_fav_empty_24);
//            Log.d("Like Button", (holder.isLiked ? "Liked" : "Unliked") + " Post ID#" + postId);
//        });

        // Comment Button Actions (opens bottom sheet)
        holder.commentButton.setOnClickListener(v -> {
            CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(postId);
            commentBottomSheet.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "CommentBottomSheet");
        });

        // Save Button Actions
        holder.saveButton.setOnClickListener(v -> {
            holder.isSaved = !holder.isSaved;
            holder.saveButton.setImageResource(holder.isSaved ? R.drawable.post_saved_filled_24 : R.drawable.post_saved_empty_24);
            Log.d("Save Button", (holder.isSaved ? "Saved" : "Unsaved") + " Post ID#" + postId);
        });

        holder.postFragmentDisplay.addView(fragmentView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postUsername, postCaption;
        ViewGroup postFragmentDisplay;
        ImageButton postersProfile, commentButton, saveButton;
        CheckBox likeButton;
        boolean isLiked = false;
        boolean isSaved = false;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postUsername = itemView.findViewById(R.id.postUsername);
            postCaption = itemView.findViewById(R.id.postCaption);
            postFragmentDisplay = itemView.findViewById(R.id.postFragmentDisplay);
            postersProfile = itemView.findViewById(R.id.postersProfile);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            saveButton = itemView.findViewById(R.id.postSaveButton);
        }
    }

    private void toggleLike(PostModel post, boolean isLiked) {
        post.setIsLiked(isLiked);
        new ToggleLikeAsync().execute(new PostModel[]{post});
    }

    //insert new team asynchronously
    private class ToggleLikeAsync extends AsyncTask<PostModel, Void, Void> {
        //update team in database and mark teams as unloaded
        protected Void doInBackground(PostModel... post) {
            PostInteractionClient postInteractionClient = SPWebApiRepository.getInstance().getPostInteractionClient();
            PostModel p = post[0];
            try {
                if (p.getIsLiked()) {
                    postInteractionClient.likePost(p.getPostId());

                } else {
                    postInteractionClient.unlikePost(p.getPostId());


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        }//end doInBackground

        //after insert is complete, reload team selector with new team
        protected void onPostExecute(Void _void) {

        }//end onPostExecute
    }//end UpdateTeamAsync

}
