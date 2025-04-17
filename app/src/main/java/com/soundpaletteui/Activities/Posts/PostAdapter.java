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
    private PostModel post;
    private int postId, userId;
    /** Tag stuff */
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
        postId = post.getPostId();

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

        // Set the Post to Display TEXT
        if (post.getPostType() == TEXT_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_text, holder.postFragmentDisplay, false);
            TextView postTextDisplay = fragmentView.findViewById(R.id.postTextDisplay);

            //Parse hex strings to int colour values
            String fontColour = post.getPostContent().getFontColour();
            String backgroundColour = post.getPostContent().getBackgroundColour();

            if (fontColour != null && !fontColour.isEmpty()) {
                postTextDisplay.setTextColor(Color.parseColor(fontColour));
            }
            if (backgroundColour != null && !backgroundColour.isEmpty()) {
                postTextDisplay.setBackgroundColor(Color.parseColor(backgroundColour));
            }

            // Set the text content
            postTextDisplay.setText(post.getPostContent().getPostTextContent());

        // Set the Post to Display IMAGE
        } else if (post.getPostType() == IMAGE_POST) {
            fragmentView = inflater.inflate(R.layout.adapter_posts_image, holder.postFragmentDisplay, false);
            ImageView postImageDisplay = fragmentView.findViewById(R.id.postImageDisplay);
            String imageName = post.getPostContent().getPostTextContent().replace(".png", "").replace(".jpg", "");
            Glide.with(context)
                    .load(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()))
                    .override(800, 800)
                    .centerCrop()
                    .into(postImageDisplay);

        // Set the Post to Display AUDIO
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

        /*
        UserClient client = SPWebApiRepository.getInstance().getUserClient();;
        UserModel user;

        client.getUserByName(postUsername, new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                UserModel user = response.body();
                int postUserId = user.getUserId();
                Log.d("PostAdapter", "UserId from body: " + postUserId);
                ImageUtils.getProfileImage(postUserId,
                        SPWebApiRepository.getInstance().getFileClient().getProfileImage(postUserId),
                        holder.postersProfile, context);
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log.e("PostAdapter", "Error retrieving UserModel object");
            }
        });
        */

        holder.likeButton.setChecked(post.getIsLiked());
        holder.likeButton.setOnClickListener(v -> toggleLike(post, holder.likeButton.isChecked()));

        holder.commentButton.setOnClickListener(v -> {
            CommentBottomSheet commentBottomSheet = CommentBottomSheet.newInstance(postId);
            commentBottomSheet.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "CommentBottomSheet");
        });

        holder.saveButton.setChecked(post.getIsSaved());
        holder.saveButton.setOnClickListener(v -> toggleSaved(post, holder.saveButton.isChecked()));

        tagScrollHandler = new Handler();
        getPostTags(holder, post);
        getUserTags(holder, post);
        holder.likeButton.setChecked(post.getIsLiked());
        holder.likeButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFA300")));

        holder.saveButton.setChecked(post.getIsSaved());
        holder.saveButton.setButtonTintList(ColorStateList.valueOf(Color.RED));

        // Button to EDIT A POST - ONLY FOR ProfileFragment
        // Check if this PostAdapter is inside ProfileFragment
        FragmentActivity activity = (FragmentActivity) context;
        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.mainScreenFrame);

        if (showEditButton) {
            holder.postEditButton.setVisibility(View.VISIBLE);

            holder.postEditButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, holder.postEditButton);
                popup.inflate(R.menu.menu_edit_post);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_edit) {
                            Log.d("EDIT POST", "user "+userId+" wants to edit post #"+postId);
                            EditPostFragment editPostFragment = EditPostFragment.newInstance(postId);
                            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                            Navigation.replaceFragment(fragmentManager, editPostFragment, "EDIT_POST_FRAGMENT", R.id.mainScreenFrame);
                            return true;
                        } else if (itemId == R.id.menu_delete) {
                            new DeletePostAsync().execute(post);
                            return true;
                        }
                        return false;
                    }
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

    // Updates the database to toogle the Post as isDeleted() and reload Posts
    private class DeletePostAsync extends AsyncTask<PostModel, Void, Void> {
        @Override
        protected Void doInBackground(PostModel... post) {
            PostClient client = SPWebApiRepository.getInstance().getPostClient();
            UserModel user = AppSettings.getInstance().getUser();
            userId = user.getUserId();
            try {
                Log.d("DELETE POST NOW", userId+" wants to the delete Post#"+postId);
                client.deletePost(postId, userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Refresh the fragment after deletion
            Log.d("REFRESH AFTER DELETE", "yes let's refresh");
            FragmentActivity activity = (FragmentActivity) context;
            Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.mainScreenFrame);
            if (currentFragment instanceof com.soundpaletteui.Activities.Profile.ProfileFragment) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                Navigation.replaceFragment(fragmentManager, new com.soundpaletteui.Activities.Profile.ProfileFragment(), "PROFILE_FRAGMENT", R.id.mainScreenFrame);
            }
        }
    }


    // Retrieves the list of tags in the post
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
                    Log.d("PostAdapter", "postTagsRecycle not found in view for this post");
                }
            });
        }).start();
    }

    // Retrieves the list of users tagged in the post
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
                    Log.d("PostAdapter", "userTagsRecycle not found in view for this post");
                }
            });
        }).start();
    }


    // Auto scrolls the horizontal list of tags
    private void startAutoScroll(PostViewHolder holder) {
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
