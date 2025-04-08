package com.soundpaletteui.Activities.Posts;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.soundpaletteui.Activities.Interactions.CommentBottomSheet;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.ApiClients.PostInteractionClient;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
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
        String likeCount = String.valueOf(post.getLikeCount());
        String commentCount = String.valueOf(post.getCommentCount());
        String postUsername = post.getUsername();

        holder.postUsername.setText(postUsername);
        holder.postCaption.setText(post.getPostCaption());
        holder.postFragmentDisplay.removeAllViews();
        holder.postLikeValue.setText(likeCount);
        holder.postCommentValue.setText(commentCount);

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
            Glide.with(context)
                    .load(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()))
                    .into(postImageDisplay);

        } else if (post.getPostType() == AUDIO_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_audio, holder.postFragmentDisplay, false);

            ImageButton playPauseButton = fragmentView.findViewById(R.id.postAudioPlayPause);
            SeekBar audioSeekBar = fragmentView.findViewById(R.id.audioSeekBar);
            audioSeekBar.setMax(100);
            String audioSource = post.getPostContent().getPostTextContent();

            playPauseButton.setOnClickListener(v ->
                    MediaPlayerManager.getInstance().playPause(audioSource, playPauseButton, audioSeekBar)
            );

            audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        MediaPlayerManager.getInstance().seekToPercent(progress);
                    }
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        } else {
            fragmentView = new View(context);
        }

        holder.postersProfile.setOnClickListener(v -> {
            ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(postUsername);
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, profileViewFragment, "PROFILE_VIEW_FRAGMENT", R.id.mainScreenFrame);
        });

        holder.likeButton.setChecked(post.getIsLiked());
        holder.likeButton.setOnClickListener(v -> toggleLike(post, holder.likeButton.isChecked()));

        holder.commentButton.setOnClickListener(v -> {
            CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(postId);
            commentBottomSheet.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "CommentBottomSheet");
        });

        holder.saveButton.setChecked(post.getIsSaved());
        holder.saveButton.setOnClickListener(v -> toggleSaved(post, holder.saveButton.isChecked()));

        holder.postFragmentDisplay.addView(fragmentView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postUsername, postCaption, postLikeValue, postCommentValue;
        ViewGroup postFragmentDisplay;
        ImageButton postersProfile, commentButton;
        CheckBox likeButton, saveButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postUsername = itemView.findViewById(R.id.postUsername);
            postCaption = itemView.findViewById(R.id.postCaption);
            postFragmentDisplay = itemView.findViewById(R.id.postFragmentDisplay);
            postersProfile = itemView.findViewById(R.id.postersProfile);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            saveButton = itemView.findViewById(R.id.postSaveButton);
            postLikeValue = itemView.findViewById(R.id.postLikeValue);
            postCommentValue = itemView.findViewById(R.id.postCommentValue);
        }
    }

    private void toggleLike(PostModel post, boolean isLiked) {
        post.setIsLiked(isLiked);
        new ToggleLikeAsync().execute(post);
    }

    private class ToggleLikeAsync extends AsyncTask<PostModel, Void, Void> {
        protected Void doInBackground(PostModel... post) {
            PostInteractionClient client = SPWebApiRepository.getInstance().getPostInteractionClient();
            try {
                if (post[0].getIsLiked()) client.likePost(post[0].getPostId());
                else client.unlikePost(post[0].getPostId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    private void toggleSaved(PostModel post, boolean isSaved) {
        post.setIsSaved(isSaved);
        new ToggleSavedAsync().execute(post);
    }

    private class ToggleSavedAsync extends AsyncTask<PostModel, Void, Void> {
        protected Void doInBackground(PostModel... post) {
            PostInteractionClient client = SPWebApiRepository.getInstance().getPostInteractionClient();
            try {
                if (post[0].getIsSaved()) client.savePost(post[0].getPostId());
                else client.unsavePost(post[0].getPostId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
