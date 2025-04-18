package com.soundpaletteui.Activities.Posts;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.soundpaletteui.Activities.Interactions.CommentBottomSheet;
import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Adapters.TagBasicAdapter;
import com.soundpaletteui.Infrastructure.Adapters.TagUserAdapter;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.SPApiServices.ApiClients.PostClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.SPApiServices.ApiClients.PostInteractionClient;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostModel> postList;
    private Context context;
    private TagBasicAdapter postTagAdapter;
    private TagUserAdapter userTagAdapter;
    private Handler tagScrollHandler;
    private int scrollPosition;
    private boolean showEditButton;
    private static final int TEXT_POST = 1;
    private static final int AUDIO_POST = 2;
    private static final int IMAGE_POST = 3;

    public PostAdapter(List<PostModel> postList) {
        this.postList = postList;
        this.showEditButton = false;
    }

    public PostAdapter(List<PostModel> postList, boolean showEditButton) {
        this.postList = postList;
        this.showEditButton = showEditButton;
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

        holder.postUsername.setText(post.getUsername());
        holder.postCaption.setText(post.getPostCaption());
        holder.postFragmentDisplay.removeAllViews();
        holder.postLikeValue.setText(String.valueOf(post.getLikeCount()));
        holder.postCommentValue.setText(String.valueOf(post.getCommentCount()));

        LayoutInflater inflater = LayoutInflater.from(context);
        View fragmentView;

        // Sets Post to display text posts
        if (post.getPostType() == TEXT_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_text, holder.postFragmentDisplay, false);
            TextView postTextDisplay = fragmentView.findViewById(R.id.postTextDisplay);

            // Assigns the image colours
            String fontColour = post.getPostContent().getFontColour();
            String backgroundColour = post.getPostContent().getBackgroundColour();

            if (fontColour != null && !fontColour.isEmpty()) {
                postTextDisplay.setTextColor(Color.parseColor(fontColour));
            }
            if (backgroundColour != null && !backgroundColour.isEmpty()) {
                postTextDisplay.setBackgroundColor(Color.parseColor(backgroundColour));
            }
            postTextDisplay.setText(post.getPostContent().getPostTextContent());

        // Sets Post to display image type posts
        } else if (post.getPostType() == IMAGE_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_image, holder.postFragmentDisplay, false);
            ImageView postImageDisplay = fragmentView.findViewById(R.id.postImageDisplay);
            String imageName = post.getPostContent().getPostTextContent().replace(".png", "").replace(".jpg", "");

            // Assigns the image here
            Glide.with(context)
                    .load(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()))
                    .override(800, 800)
                    .centerCrop()
                    .into(postImageDisplay);

        // Sets Post to display audio type posts
        } else if (post.getPostType() == AUDIO_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_audio, holder.postFragmentDisplay, false);

            // Assigns the audio file here
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

        // Sets the Post and User tags in the post
        getPostTags(holder, post);
        getUserTags(holder, post);

        // Goes to the user's profile who the post belongs to
        holder.postersProfile.setOnClickListener(v -> {
            ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(post.getUsername());
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, profileViewFragment, "PROFILE_VIEW_FRAGMENT", R.id.mainScreenFrame);
        });

        // Assigns the user's profile picture from S3
        UserClient client = SPWebApiRepository.getInstance().getUserClient();
        client.getUserByName(post.getUsername(), new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                UserModel user = response.body();
                if (user != null) {
                    ImageUtils.getProfileImage(user.getUserId(),
                            SPWebApiRepository.getInstance().getFileClient().getProfileImage(user.getUserId()),
                            holder.postersProfile, context);
                }
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("PostAdapter", "Error retrieving UserModel object");
            }
        });

        // Like button action
        holder.likeButton.setChecked(post.getIsLiked());
        holder.likeButton.setOnClickListener(v -> toggleLike(post, holder.likeButton.isChecked()));
        holder.likeButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFA300")));

        // Comment button action
        holder.commentButton.setOnClickListener(v -> {
            CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(postId);
            commentBottomSheet.setOnCommentAddedListener(newCount -> {
                post.setCommentCount(newCount);
                notifyItemChanged(holder.getAdapterPosition());
            });
            commentBottomSheet.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "CommentBottomSheet");
        });


        // Save button action
        holder.saveButton.setChecked(post.getIsSaved());
        holder.saveButton.setOnClickListener(v -> toggleSaved(post, holder.saveButton.isChecked()));
        holder.saveButton.setButtonTintList(ColorStateList.valueOf(Color.RED));

        // Editing a Post action -- only available on the ProfileFragment under "POSTS" toggle button
        if (showEditButton) {
            holder.postEditButton.setVisibility(View.VISIBLE);
            holder.postEditButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, holder.postEditButton);
                popup.inflate(R.menu.menu_edit_post);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_edit) {
                        Log.d("EDIT POST", "user wants to edit post #" + postId);
                        EditPostFragment editPostFragment = EditPostFragment.newInstance(postId);
                        FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                        Navigation.replaceFragment(fragmentManager, editPostFragment, "EDIT_POST_FRAGMENT", R.id.mainScreenFrame);
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
                        new DeletePostAsync(post, context).execute();
                        ProfileFragment profileFragment = new ProfileFragment();
                        FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                        Navigation.replaceFragment(fragmentManager, profileFragment, "EDIT_POST_FRAGMENT", R.id.mainScreenFrame);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        } else {
            holder.postEditButton.setVisibility(View.GONE);
        }

        holder.postFragmentDisplay.addView(fragmentView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postUsername, postCaption, postLikeValue, postCommentValue;
        ViewGroup postFragmentDisplay;
        ImageButton postersProfile, commentButton, postEditButton;
        CheckBox likeButton, saveButton;
        RecyclerView postTagsRecycle, userTagsRecycle;

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
            postTagsRecycle = itemView.findViewById(R.id.postTagsRecycle);
            userTagsRecycle = itemView.findViewById(R.id.userTagsRecycle);
            postEditButton = itemView.findViewById(R.id.postEditButton);
        }
    }

    // Connect to API Server and set the Post as isDeleted == TRUE
    private class DeletePostAsync extends AsyncTask<Void, Void, Void> {
        private final PostModel post;
        private final Context context;

        public DeletePostAsync(PostModel post, Context context) {
            this.post = post;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PostClient client = SPWebApiRepository.getInstance().getPostClient();
            UserModel user = AppSettings.getInstance().getUser();
            int userId = user.getUserId();
            int postId = post.getPostId();
            try {
                client.deletePost(postId, userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }


    // Like function with API Server
    private void toggleLike(PostModel post, boolean isLiked) {
        int position = postList.indexOf(post);
        post.setIsLiked(isLiked);

        // Update like count locally for immediate UI feedback
        int currentLikeCount = post.getLikeCount();
        if (isLiked) {
            post.setLikeCount(currentLikeCount + 1);
        } else {
            post.setLikeCount(currentLikeCount - 1);
        }

        // Refresh the UI
        notifyItemChanged(position);
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


    // Saved function with API Server
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

    // Get the Post Tags and set them to the Adapter
    private void getPostTags(PostViewHolder holder, PostModel post) {
        new Thread(() -> {
            List<TagModel> postTagsList = post.getPostTags();
            ((Activity) context).runOnUiThread(() -> {
                if (postTagsList != null) {
                    postTagAdapter = new TagBasicAdapter((ArrayList<TagModel>) postTagsList, context);
                    holder.postTagsRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    holder.postTagsRecycle.setAdapter(postTagAdapter);
                    if (holder.postTagsRecycle.getOnFlingListener() == null) {
                        SnapHelper snapHelper = new LinearSnapHelper();
                        snapHelper.attachToRecyclerView(holder.postTagsRecycle);
                    }
                    startAutoScroll(holder);
                } else {
                    holder.postTagsRecycle.setAdapter(null);
                }
            });
        }).start();
    }

    // Get the User tags and set them to the Adapter
    private void getUserTags(PostViewHolder holder, PostModel post) {
        new Thread(() -> {
            List<String> userTagsList = post.getPostUserTags();
            ((Activity) context).runOnUiThread(() -> {
                if (userTagsList != null) {
                    userTagAdapter = new TagUserAdapter(userTagsList, context);
                    holder.userTagsRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    holder.userTagsRecycle.setAdapter(userTagAdapter);
                    if (holder.userTagsRecycle.getOnFlingListener() == null) {
                        SnapHelper snapHelper = new LinearSnapHelper();
                        snapHelper.attachToRecyclerView(holder.userTagsRecycle);
                    }
                    startAutoScroll(holder);
                } else {
                    holder.userTagsRecycle.setAdapter(null);
                }
            });
        }).start();
    }

    // Auto scroll for Post Tags
    private void startAutoScroll(PostViewHolder holder) {
        tagScrollHandler = new Handler();
        tagScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;
                if (postTagAdapter != null && postTagAdapter.getItemCount() > 0) {
                    if (scrollPosition < postTagAdapter.getItemCount()) {
                        holder.postTagsRecycle.smoothScrollToPosition(scrollPosition++);
                    } else {
                        scrollPosition = 0;
                        holder.postTagsRecycle.smoothScrollToPosition(scrollPosition);
                    }
                }
                if (userTagAdapter != null && userTagAdapter.getItemCount() > 0) {
                    if (scrollPosition < userTagAdapter.getItemCount()) {
                        holder.userTagsRecycle.smoothScrollToPosition(scrollPosition++);
                    } else {
                        scrollPosition = 0;
                        holder.userTagsRecycle.smoothScrollToPosition(scrollPosition);
                    }
                }
                tagScrollHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }
}
