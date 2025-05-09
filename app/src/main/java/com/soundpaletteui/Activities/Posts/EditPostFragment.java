package com.soundpaletteui.Activities.Posts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.SearchAdapters.TagSearchAdapter;
import com.soundpaletteui.Activities.SearchAdapters.TagSelectedAdapter;
import com.soundpaletteui.Activities.SearchAdapters.UserSearchAdapter;
import com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter;
import com.soundpaletteui.Infrastructure.Models.Post.PostContentModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.ImageUtils;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays the Edit Post screen where users can update the caption, content, colours, tags, and users of an existing post.
public class EditPostFragment extends Fragment {
    private int postId;
    private PostModel currentPost;
    private UserModel user;
    private int userId;
    private List<TagModel> tags;
    private Button previewButton;
    private EditText captionInput, textContentInput, tagSearchInput, userSearchInput;
    private CheckBox isMatureCheckbox, followerOnlyCheckbox;
    private RecyclerView selectedPostTags, selectedUserTags, postTagSearchResult, userSearchResult;
    private TagSelectedAdapter selectedTagsAdapter;
    private UserSelectedAdapter selectedUsersAdapter;
    private TagSearchAdapter tagSearchAdapter;
    private UserSearchAdapter userSearchAdapter;

    private MaterialButton backgroundColourSelector, fontColourSelector;
    private View backgroundColourDisplay, fontColourDisplay;

    private List<TagModel> selectedTags = new ArrayList<>();
    private List<TagModel> searchTags = new ArrayList<>();
    private List<String> selectedUsers = new ArrayList<>();
    private List<String> searchResults = new ArrayList<>();

    private String backgroundHex = "#FFFFFF";
    private String fontHex = "#000000";
    private boolean selectingBackground = true;

    // Creates a new instance of EditPostFragment with the information about which post to edit
    public static EditPostFragment newInstance(int postId) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle args = new Bundle();
        args.putInt("POST_ID", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the post ID that was sent to this screen
        if (getArguments() != null) {
            postId = getArguments().getInt("POST_ID");
        }

        // Get the current user
        user = AppSettings.getInstance().getUser();
        userId = user.getUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the new wrapped layout
        View rootView = inflater.inflate(R.layout.fragment_post_create, container, false);

        // Apply emoji + hue gradient
        View rootLayout = rootView.findViewById(R.id.root_layout);
        boolean isDark = DarkModePreferences.isDarkModeEnabled(rootLayout.getContext());
        UISettings.applyBrightnessGradientBackground(rootLayout, 260f, isDark);

        com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView emojiBg = rootView.findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBg.setAlpha(0.65f);

        // Wire up preview button & start loading the post
        previewButton = rootView.findViewById(R.id.previewButton);
        previewButton.setOnClickListener(v -> showPostPreview());
        new LoadPostTask(rootView).execute();

        return rootView;
    }


    // Loads the post information from the server in the background
    private class LoadPostTask extends AsyncTask<Void, Void, PostModel> {
        private final View rootView;
        LoadPostTask(View rootView) { this.rootView = rootView; }

        @Override
        protected PostModel doInBackground(Void... voids) {
            try {
                PostModel post = SPWebApiRepository.getInstance().getPostClient().getPost(postId);
                return post;
            } catch (IOException e) {
                Log.e("EditPostFragment", "Failed to load post", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PostModel post) {
            // Load the post
            if (post != null) {
                currentPost = post;
                loadPostContent(rootView, currentPost.getPostType());
                populateFields(rootView);
                showPostPreview(); // Show preview on load

            // If post does not exist, show an alert and go back
            } else {
                if (getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Post Deleted")
                            .setMessage("This post no longer exists or has been deleted.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                if (getFragmentManager() != null) {
                                    getFragmentManager().popBackStack(); // navigate back safely
                                }
                            })
                            .show();
                } else {
                    Log.e("EditPostFragment", "Context is null, can't show dialog.");
                }
            }
        }
    }

    // Shows a live preview of the post based on current inputs
    private void showPostPreview() {
        if (currentPost == null || currentPost.getPostContent() == null) {
            Toast.makeText(getContext(), "Cannot preview post. Post data is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update post values from input
        String previewCaption = captionInput != null ? captionInput.getText().toString().trim() : currentPost.getPostCaption();
        String previewContent = textContentInput != null ? textContentInput.getText().toString().trim() : currentPost.getPostContent().getPostTextContent();

        PostContentModel contentModel = new PostContentModel(previewContent, backgroundHex, fontHex);

        PostModel previewPost = new PostModel(
                currentPost.getPostId(),
                previewCaption,
                new ArrayList<>(selectedTags),
                contentModel,
                currentPost.getCreatedDate(),
                currentPost.getUsername(),
                currentPost.getPostType(),
                currentPost.getCommentCount(),
                currentPost.getLikeCount(),
                currentPost.getIsLiked(),
                currentPost.getIsSaved(),
                currentPost.getFileId(),
                new ArrayList<>(selectedUsers)
        );

        // Inflate and show preview view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View previewView = inflater.inflate(R.layout.adapter_posts, null);
        ViewGroup fragmentDisplay = previewView.findViewById(R.id.postFragmentDisplay);
        fragmentDisplay.removeAllViews();

        TextView username = previewView.findViewById(R.id.postUsername);
        TextView caption = previewView.findViewById(R.id.postCaption);
        username.setText(previewPost.getUsername());
        caption.setText(previewPost.getPostCaption());

        // Depending on the post type, load the correct preview
        if (previewPost.getPostType() == 1) {           // Text posts
            View postTextView = inflater.inflate(R.layout.adapter_posts_text, fragmentDisplay, false);
            TextView textContent = postTextView.findViewById(R.id.postTextDisplay);
            textContent.setText(previewPost.getPostContent().getPostTextContent());
            try {
                postTextView.setBackgroundColor(Color.parseColor(backgroundHex));
                textContent.setTextColor(Color.parseColor(fontHex));
            } catch (IllegalArgumentException e) {
                Log.e("PreviewError", "Invalid colour hex: " + e.getMessage());
            }
            fragmentDisplay.addView(postTextView);
        }
        else if(previewPost.getPostType() == 3) {       // Image posts
            View postContentView = inflater.inflate(R.layout.adapter_posts_image, fragmentDisplay, false);

            // Get file name (so it can source it from drawables)
            try {
                ImageView postImageDisplay = postContentView.findViewById(R.id.postImageDisplay);
                ImageUtils.getPostImage(SPWebApiRepository.getInstance().getFileClient().getPostFile(previewPost.getFileId()), postImageDisplay, requireContext());
            } catch (Exception e) {}

            fragmentDisplay.addView(postContentView);
        }

        FrameLayout previewContainer = requireView().findViewById(R.id.postPreviewContainer);
        previewContainer.removeAllViews();
        previewContainer.addView(previewView);
    }

    // Loads the correct layout depending on the type of post
    private void loadPostContent(View rootView, int postType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout postContentContainer = rootView.findViewById(R.id.postContentContainer);
        postContentContainer.removeAllViews();

        View postContentView = null;
        if (postType == 1) {            // Text post
            postContentView = inflater.inflate(R.layout.fragment_post_create_text, postContentContainer, false);
            backgroundColourDisplay = postContentView.findViewById(R.id.backgroundColourDisplay);
            fontColourDisplay = postContentView.findViewById(R.id.fontColourDisplay);
            backgroundColourSelector = postContentView.findViewById(R.id.backgroundColourSelector);
            fontColourSelector = postContentView.findViewById(R.id.fontColourSelector);

            backgroundColourSelector.setOnClickListener(v -> openColourPicker(true));
            fontColourSelector.setOnClickListener(v -> openColourPicker(false));
        }
        else if (postType == 3) {       // Image post
            postContentView = inflater.inflate(R.layout.adapter_posts_image, postContentContainer, false);
            ImageView postImageDisplay = postContentView.findViewById(R.id.postImageDisplay);
            int fileId = currentPost.getFileId();
            Log.d("EditPostFragment", "currentPost: " + fileId);
            ImageUtils.getPostImage(fileId, SPWebApiRepository.getInstance().getFileClient().getPostFile(fileId),
                    postImageDisplay,
                    requireContext());
        }
        else {
            postContentView = inflater.inflate(R.layout.fragment_post_create_media, postContentContainer, false);

            // Disable file selection for audio/image posts
            TextView mediaContent = postContentView.findViewById(R.id.mediaContent);
            ImageButton mediaButton = postContentView.findViewById(R.id.mediaButton);
            LinearLayout linearLayout = postContentView.findViewById(R.id.linearLayout);

            Context context = linearLayout.getContext();
            linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.white_translucency_gradient_create_grey));

            if (mediaContent != null) {
                mediaContent.setEnabled(false);
                mediaContent.setFocusable(false);
                mediaContent.setClickable(false);
                mediaContent.setText(currentPost.getPostContent().getPostTextContent());
                mediaContent.setTextColor(ContextCompat.getColor(context, R.color.black));
            }

            if (mediaButton != null) {
                mediaButton.setEnabled(false);
            }
        }

        postContentContainer.addView(postContentView);
    }

    // Fills in the inputs with the post's current information
    private void populateFields(View rootView) {
        captionInput = rootView.findViewById(R.id.caption);
        textContentInput = rootView.findViewById(R.id.textContent);
        isMatureCheckbox = rootView.findViewById(R.id.isMature);
        followerOnlyCheckbox = rootView.findViewById(R.id.isFollowing);
        postTagSearchResult = rootView.findViewById(R.id.postTagSearchResult);
        selectedPostTags = rootView.findViewById(R.id.selectedPostTags);
        userSearchResult = rootView.findViewById(R.id.userSearchResult);
        selectedUserTags = rootView.findViewById(R.id.selectedUserTags);

        // Set caption, checkboxes, and content
        captionInput.setText(currentPost.getPostCaption());
        isMatureCheckbox.setChecked(currentPost.getIsLiked());
        followerOnlyCheckbox.setChecked(currentPost.getIsSaved());

        // if text post and there is a PostContent
        // then Set the font and background colour
        if (currentPost.getPostType() == 1 && currentPost.getPostContent() != null) {
            textContentInput.setText(currentPost.getPostContent().getPostTextContent());
            backgroundHex = currentPost.getPostContent().getBackgroundColour();
            fontHex = currentPost.getPostContent().getFontColour();
            if (backgroundColourDisplay != null) backgroundColourDisplay.setBackgroundColor(Color.parseColor(backgroundHex));
            if (fontColourDisplay != null) fontColourDisplay.setBackgroundColor(Color.parseColor(fontHex));

        // See the message there are no posts
        } else {
            currentPost.setPostCaption(captionInput.getText().toString());
        }

        // Sets up the adapters for tags and users
        selectedTags = new ArrayList<>(currentPost.getPostTags());
        selectedUsers = new ArrayList<>(currentPost.getPostUserTags());

        tagSearchAdapter = new TagSearchAdapter(searchTags, tag -> {
            if (!selectedTags.contains(tag)) {
                selectedTags.add(tag);
                searchTags.remove(tag);
                selectedTagsAdapter.notifyDataSetChanged();
                tagSearchAdapter.notifyDataSetChanged();
            }
        });

        selectedTagsAdapter = new TagSelectedAdapter(selectedTags, tag -> {
            selectedTags.remove(tag);
            searchTags.add(tag);
            selectedTagsAdapter.notifyDataSetChanged();
            tagSearchAdapter.notifyDataSetChanged();
        });

        postTagSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        postTagSearchResult.setAdapter(tagSearchAdapter);
        selectedPostTags.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedPostTags.setAdapter(selectedTagsAdapter);

        // User search adapter
        userSearchAdapter = new UserSearchAdapter(searchResults, user -> {
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user);
                searchResults.remove(user);
                selectedUsersAdapter.notifyDataSetChanged();
                userSearchAdapter.notifyDataSetChanged();
            }
        });

        selectedUsersAdapter = new UserSelectedAdapter(selectedUsers, user -> {
            selectedUsers.remove(user);
            searchResults.add(user);
            selectedUsersAdapter.notifyDataSetChanged();
            userSearchAdapter.notifyDataSetChanged();
        });

        userSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchResult.setAdapter(userSearchAdapter);
        selectedUserTags.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedUserTags.setAdapter(selectedUsersAdapter);

        // Load the full list of tags
        new GetTagsAsync() {
            @Override
            protected void onPostExecute(Void result) {
                searchTags.removeAll(selectedTags);
                tagSearchAdapter.notifyDataSetChanged();
            }
        }.execute();

        // Make the preview button visible
        Button previewButton = rootView.findViewById(R.id.previewButton);
        previewButton.setVisibility(View.VISIBLE);
        previewButton.performClick();

        // Set up the save button
        Button postButton = rootView.findViewById(R.id.postButton);
        postButton.setText("Save");
        postButton.setOnClickListener(v -> updatePost());
    }

    // Updates the post with the new information entered by the user
    private void updatePost() {
        String caption = captionInput.getText().toString().trim();
        String content = (textContentInput != null) ? textContentInput.getText().toString().trim() : "";
        boolean isMature = isMatureCheckbox.isChecked();
        boolean isPremium = followerOnlyCheckbox.isChecked();

        // Make sure the caption and content are not empty
        if (currentPost.getPostCaption().isEmpty() || (currentPost.getPostType() == 1 && content.isEmpty())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Invalid Input")
                    .setMessage("Caption and content cannot be empty.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Create updated content model
        PostContentModel updatedContent = new PostContentModel(content, backgroundHex, fontHex);

        // Create the updated post model
        PostModel updatedPost = new PostModel(
                currentPost.getPostId(), caption, selectedTags, updatedContent,
                currentPost.getCreatedDate(), currentPost.getUsername(),
                currentPost.getPostType(), currentPost.getCommentCount(),
                currentPost.getLikeCount(), isMature, isPremium, currentPost.getFileId(), selectedUsers
        );

        new UpdatePostTask().execute(updatedPost);
    }

    // Async task to save the updated post to the server
    private class UpdatePostTask extends AsyncTask<PostModel, Void, Boolean> {
        @Override
        protected Boolean doInBackground(PostModel... post) {
            try {
                SPWebApiRepository.getInstance().getPostClient().updatePost(post[0]);
                return true;
            } catch (IOException e) {
                Log.e("EditPostFragment", "Failed to update post", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                Navigation.replaceFragment(requireActivity().getSupportFragmentManager(), new ProfileFragment(), "PROFILE_FRAGMENT", R.id.mainScreenFrame);
            } else {
                Toast.makeText(getContext(), "Failed to update post", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Opens a colour picker for either background or font colour
    private void openColourPicker(boolean background) {
        selectingBackground = background;
        new ColorPickerDialog.Builder(requireContext())
                .setTitle("Select Colour")
                .setPreferenceName("MyColorPicker")
                .setPositiveButton("OK", (ColorEnvelopeListener) (envelope, fromUser) -> {
                    String hexCode = "#" + envelope.getHexCode();
                    if (selectingBackground) {
                        backgroundHex = hexCode;
                        backgroundColourDisplay.setBackgroundColor(envelope.getColor());
                    } else {
                        fontHex = hexCode;
                        fontColourDisplay.setBackgroundColor(envelope.getColor());
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }

    // Async task to get all available tags
    private class GetTagsAsync extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                TagClient client = SPWebApiRepository.getInstance().getTagClient();
                tags = client.getTags();
                requireActivity().runOnUiThread(() -> {
                    searchTags.clear();
                    searchTags.addAll(tags);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}