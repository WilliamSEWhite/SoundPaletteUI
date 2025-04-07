package com.soundpaletteui.Activities.Posts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.media.MediaScannerConnection;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.Messages.NewChatroomFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.SearchAdapters.TagSearchAdapter;
import com.soundpaletteui.Activities.SearchAdapters.TagSelectedAdapter;
import com.soundpaletteui.Activities.SearchAdapters.UserSearchAdapter;
import com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.PostContentModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostFragment extends Fragment {
    private int postPrompt, postType;
    private UserModel user;
    private List<TagModel> tags;
    private int userId;
    private List<String> selectedUsers = new ArrayList<>();
    private List<String> searchResults = new ArrayList<>();
    private List<TagModel> selectedTags = new ArrayList<>();
    private List<TagModel> searchTags = new ArrayList<>();
    private TagSearchAdapter tagSearchAdapter;
    private TagSelectedAdapter selectedTagsAdapter;
    private UserSearchAdapter userSearchAdapter;
    private UserSelectedAdapter selectedUsersAdapter;
    private PostContentModel postContentModel;
    String caption, postContent;
    boolean isPremium, isMature;
    private View backgroundColourDisplay, fontColourDisplay;
    private TextView postMediaContext;
    private EditText postCaption, postTextContext, tagSearchInput, userSearchInput;
    private RecyclerView postTagSearchResult, selectedPostTags, userSearchResult, selectedUserTags;
    private Button previewButton, postButton;
    private ImageButton mediaButton;
    private MaterialButton backgroundColourSelector, fontColourSelector;
    private CheckBox isMatureCheckbox, followerOnlyCheckbox;
    private int defaultBackgroundColor = 0xFFFFFFFF;
    private int defaultFontColor = 0xFF000000;
    private String backgroundHex = "#FFFFFF";
    private String fontHex = "#000000";
    private boolean selectingBackground = true;

    private ActivityResultLauncher<Intent> mediaPickerLauncher;

    public CreatePostFragment() {}

    public static CreatePostFragment newInstance(int postPrompt) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putInt("postPrompt", postPrompt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postPrompt = getArguments().getInt("postPrompt", -1);
        }

        if (postPrompt == 1) {
            postType = 1;
        } else {
            // Set up the media picker so user can select an image or audio file
            mediaPickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri selectedUri = result.getData().getData();
                            if (selectedUri != null) {
                                String mimeType = requireContext().getContentResolver().getType(selectedUri);

                                // Check if the file type is an image or audio, and assign the postType accordingly
                                if (mimeType != null) {
                                    if (mimeType.startsWith("image/")) {
                                        postType = 3;
                                        Log.d("Create Media Post", "Post Type Assigned: "+postType);
                                    } else if (mimeType.startsWith("audio/")) {
                                        postType = 2;
                                        Log.d("Create Media Post", "Post Type Assigned: "+postType);
                                    } else {
                                        // Send alert  dialog, file not allowed
                                        Log.d("Create Media Post", "There was an error with the file selected.");
                                        new AlertDialog.Builder(requireContext())
                                                .setTitle("Invalid Selection")
                                                .setMessage("The file you selected is not valid.")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }

                                // Assign the file name to the TextView
                                postContent = getFileNameFromUri(selectedUri);
                                postMediaContext.setText(postContent);
                                Log.d("postContent", postContent);
                            }
                        }
                    }
            );
        }
    }

    // Get the name of the file
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex).toLowerCase();
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }

        result = result.toLowerCase().replace(".png", "").replace(".jpg", "");
        return result;
    }


    // Inflate the view with Create Post Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_create, container, false);
        user = AppSettings.getInstance().getUser();
        userId = user.getUserId();

        loadPostContent(rootView, postType);

        postCaption = rootView.findViewById(R.id.caption);
        isMatureCheckbox = rootView.findViewById(R.id.isMature);
        followerOnlyCheckbox = rootView.findViewById(R.id.isFollowing);

        // SET UP POST TAGS
        tagSearchInput = rootView.findViewById(R.id.tagSearchInput);
        postTagSearchResult = rootView.findViewById(R.id.postTagSearchResult);
        selectedPostTags = rootView.findViewById(R.id.selectedPostTags);
        new GetTagsAsync().execute();
        tagSearchAdapter = new TagSearchAdapter(searchTags, tag -> {
            if (!selectedTags.contains(tag)) {
                selectedTags.add(tag);
                selectedTagsAdapter.notifyDataSetChanged();
            }
        });
        selectedTagsAdapter = new TagSelectedAdapter(selectedTags, tag -> {
            selectedTags.remove(tag);
            selectedTagsAdapter.notifyDataSetChanged();
        });
        postTagSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        postTagSearchResult.setAdapter(tagSearchAdapter);
        selectedPostTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedPostTags.setAdapter(selectedTagsAdapter);

        // SET UP USER TAGS
        userSearchInput = rootView.findViewById(R.id.userSearchInput);
        userSearchResult = rootView.findViewById(R.id.userSearchResult);
        selectedUserTags = rootView.findViewById(R.id.selectedUserTags);
        populateDummyUsersAsync();
        userSearchAdapter = new UserSearchAdapter(searchResults, user -> {
            if (!selectedUsers.contains(user)) {
                selectedUsers.add(user);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });
        selectedUsersAdapter = new UserSelectedAdapter(selectedUsers, user -> {
            selectedUsers.remove(user);
            selectedUsersAdapter.notifyDataSetChanged();
        });
        userSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchResult.setAdapter(userSearchAdapter);
        selectedUserTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        selectedUserTags.setAdapter(selectedUsersAdapter);

        // PREVIEW BUTTON
        previewButton = rootView.findViewById(R.id.previewButton);
        previewButton.setOnClickListener(v -> {
            getNewPostDetails(postType);
            PostModel newPost = new PostModel(0, caption, new ArrayList<>(selectedTags), postContentModel, new Date(), user.getUsername(), postType, 0, 0, false, false
            );

            showPostPreview(newPost);
        });

        // POST BUTTON
        postButton = rootView.findViewById(R.id.postButton);
        postButton.setOnClickListener(v -> {
            getNewPostDetails(postType);
            savePost();
        });

        return rootView;
    }

    private void loadPostContent(View rootView, int postType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout postContentContainer = rootView.findViewById(R.id.postContentContainer);
        postContentContainer.removeAllViews();

        View postContentView;
        if (postType == 1) {
            postContentView = inflater.inflate(R.layout.fragment_post_create_text, postContentContainer, false);
            backgroundColourDisplay = postContentView.findViewById(R.id.backgroundColourDisplay);
            fontColourDisplay = postContentView.findViewById(R.id.fontColourDisplay);
            backgroundColourSelector = postContentView.findViewById(R.id.backgroundColourSelector);
            fontColourSelector = postContentView.findViewById(R.id.fontColourSelector);
            backgroundColourSelector.setOnClickListener(v -> openColourPicker(true));
            fontColourSelector.setOnClickListener(v -> openColourPicker(false));
            postTextContext = postContentView.findViewById(R.id.textContent);
        } else {
            postContentView = inflater.inflate(R.layout.fragment_post_create_media, postContentContainer, false);
            mediaButton = postContentView.findViewById(R.id.mediaButton);
            postMediaContext = postContentView.findViewById(R.id.mediaContent);

            mediaButton.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Scanning media folders...", Toast.LENGTH_SHORT).show();

                String[] foldersToScan = new String[]{
                        "/sdcard/Pictures",
                        "/sdcard/DCIM/Camera",
                        "/sdcard/Download",
                        "/sdcard/Music",
                        "/sdcard/Recordings"
                };

                MediaScannerConnection.scanFile(
                        requireContext(),
                        foldersToScan,
                        null,
                        (path, uri) -> {
                            Log.d("MediaScanner", "Scanned: " + path + " -> " + uri);

                            // After final scan completes, add small delay then launch picker
                            if (path.equals(foldersToScan[foldersToScan.length - 1])) {
                                requireActivity().runOnUiThread(() -> {
                                    // Delay by 500ms to allow media indexing to complete
                                    new android.os.Handler().postDelayed(() -> {
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.setType("*/*");
                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "audio/*"});
                                        mediaPickerLauncher.launch(Intent.createChooser(intent, "Select Media"));
                                    }, 500);
                                });
                            }
                        }
                );
            });
        }
        postContentContainer.addView(postContentView);
    }

    private void getNewPostDetails(int postType) {
        caption = postCaption.getText().toString();
        isMature = isMatureCheckbox.isChecked();
        isPremium = followerOnlyCheckbox.isChecked();

        if (postType == 1) {
            postContent = postTextContext.getText().toString();
        } else if (postType == 2 || postType == 3) {
            postContent = postMediaContext.getText().toString();
        } else {
            // Send alert  dialog, file not allowed
            Log.d("Create Media Post", "Preview Post Type Assigned: "+postType);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Invalid Selection")
                    .setMessage("The file you selected is not valid.")
                    .setPositiveButton("OK", null)
                    .show();
        }
        postContentModel = new PostContentModel(postContent);
    }

    // Display a preview of the user's post
    private void showPostPreview(PostModel previewPost) {
        if (previewPost.getPostContent() == null) {
            Log.e("PreviewError", "Post content is null!");
            return;
        }

        // Setting up the view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View previewView = inflater.inflate(R.layout.adapter_posts, null, false);

        ViewGroup fragmentDisplay = previewView.findViewById(R.id.postFragmentDisplay);
        fragmentDisplay.removeAllViews();

        // Set the username, caption and post context
        TextView username = previewView.findViewById(R.id.postUsername);
        TextView caption = previewView.findViewById(R.id.postCaption);
        username.setText(previewPost.getUsername());
        caption.setText(previewPost.getPostCaption());

        // Setting the user's selected background and font colours for TEXT POSTS ONLY
        // Note: When colours added to database, should feed colours as parameter into Post Models, and assign in PostAdapter
        if (postType==1) {
            View postContentView = inflater.inflate(R.layout.adapter_posts_text, fragmentDisplay, false);
            TextView postText = postContentView.findViewById(R.id.postTextDisplay);
            postText.setText(previewPost.getPostContent().getPostTextContent());
            try {
                postContentView.setBackgroundColor(Color.parseColor(backgroundHex));
                postText.setTextColor(Color.parseColor(fontHex));
            } catch (IllegalArgumentException e) {
                Log.e("PreviewError", "Invalid colour hex: " + e.getMessage());
            }

            fragmentDisplay.addView(postContentView);
        } else if (postType == 3) {
            View postContentView = inflater.inflate(R.layout.adapter_posts_image, fragmentDisplay, false);

            // Get file name (so it can source it from drawables)
            try {
                ImageView postImageDisplay = postContentView.findViewById(R.id.postImageDisplay);
                int imageResource = getResources().getIdentifier(postContent, "drawable", getContext().getPackageName());
                Log.d("PreviewImage", "Going to load "+postContent+ "#" +imageResource);
                postImageDisplay.setImageResource(imageResource);
            } catch (Exception e) {
                Log.e("PreviewError", "Failed to load image from URI: " + postContent, e);
            }
            fragmentDisplay.addView(postContentView);
        }
        FrameLayout previewContainer = requireView().findViewById(R.id.postPreviewContainer);
        previewContainer.removeAllViews();
        previewContainer.addView(previewView);
    }

    private void savePost() {
        caption = postCaption.getText().toString().trim();
        boolean hasTextContent = postPrompt == 1 && postTextContext != null && !postTextContext.getText().toString().trim().isEmpty();
        boolean hasMediaContent = postPrompt != 1 && postMediaContext != null && !postMediaContext.getText().toString().trim().isEmpty();

        if (postType != 1 && postType != 2 && postType != 3) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Invalid Post")
                    .setMessage("Please select a valid post type before publishing.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }else if (caption.isEmpty() || (!hasTextContent && !hasMediaContent)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Incomplete Post")
                    .setMessage("Please ensure your post has a caption and content before publishing.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        getNewPostDetails(postType);
        NewPostModel newPost = new NewPostModel(
                userId, postType, caption, isPremium, isMature, new Date(), new Date(), new ArrayList<>(selectedTags), postContent
        );

        new MakePostAsync().execute(newPost);
    }


    private class GetTagsAsync extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... d) {
            try {
                TagClient client = SPWebApiRepository.getInstance().getTagClient();
                tags = client.getTags();
                requireActivity().runOnUiThread(() -> {
                    searchTags.clear();
                    searchTags.addAll(tags);
                    tagSearchAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    private class MakePostAsync extends AsyncTask<NewPostModel, Void, Void> {
        protected Void doInBackground(NewPostModel... d) {
            try {
                PostClient client = SPWebApiRepository.getInstance().getPostClient();
                client.makePost(d[0]);
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error making post", Toast.LENGTH_SHORT).show()
                );
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            replaceMainFragment(new ProfileFragment(), "Go to ProfileFragment");
        }
    }

    private void replaceMainFragment(Fragment new_fragment, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, new_fragment, tag, R.id.mainScreenFrame);
    }


    private void openColourPicker(boolean background) {
        selectingBackground = background;
        new ColorPickerDialog.Builder(requireContext())
                .setTitle("Select Colour")
                .setPreferenceName("MyColorPicker")
                .setPositiveButton("OK", (ColorEnvelopeListener) (envelope, fromUser) -> {
                    int selectedColor = envelope.getColor();
                    String hexCode = "#" + envelope.getHexCode();
                    if (selectingBackground) {
                        backgroundHex = hexCode;
                        defaultBackgroundColor = selectedColor;
                        backgroundColourDisplay.setBackgroundColor(defaultBackgroundColor);
                    } else {
                        fontHex = hexCode;
                        defaultFontColor = selectedColor;
                        fontColourDisplay.setBackgroundColor(defaultFontColor);
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }


    private void populateDummyUsersAsync() {
        new Thread(() -> {
            try {
                UserClient client = SPWebApiRepository.getInstance().getUserClient();
                List<String> results = new ArrayList<>();
                results.add("user1");
                results.add("user2");
                results.add("user3");
                results.add("user4");
                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("NewPostFragment", "Failed to load dummy users: " + e.getMessage());
            }
        }).start();
    }
}
