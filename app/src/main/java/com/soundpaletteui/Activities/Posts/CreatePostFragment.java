package com.soundpaletteui.Activities.Posts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;

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
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Models.FileModel;
import com.soundpaletteui.Infrastructure.Utilities.FileUtils;
import com.soundpaletteui.Infrastructure.Utilities.PostUtils;
import com.soundpaletteui.SPApiServices.ApiClients.PostClient;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Post.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostContentModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;
import com.soundpaletteui.Views.EmojiBackgroundView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private String backgroundColour = "#FFFFFF";
    private String fontColour = "#000000";
    private boolean selectingBackground = true;
    private Uri selectedUri;
    private int fileId = 0;
    private FileModel fileModel;

    private ActivityResultLauncher<Intent> mediaPickerLauncher;
    private static final int MEDIA_PERMISSION_REQUEST_CODE = 101;
    private static final int LEGACY_STORAGE_PERMISSION_REQUEST_CODE = 102;

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
            Log.d("CreatePostFragment", "postPrompt: " + postPrompt);
        }

        if (postPrompt == 1) {
            Log.d("CreatePostFragment", "Text Post");
            postType = 1;
        } else {
            // Set up the media picker so user can select an image or audio file
            mediaPickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            selectedUri = result.getData().getData();
                            if (selectedUri != null) {
                                String mimeType = requireContext().getContentResolver().getType(selectedUri);

                                // Check if the file type is an image or audio, and assign the postType accordingly
                                if (mimeType != null) {
                                    if (mimeType.startsWith("image/")) {
                                        postType = 3;
                                        fileId = 3;
                                        Log.d("CreatePostFragment", "Post Type Assigned: "+postType);
                                    } else if (mimeType.startsWith("audio/")) {
                                        postType = 2;
                                        fileId = 2;
                                        Log.d("CreatePostFragment", "Post Type Assigned: "+postType);
                                    } else {
                                        // Send alert  dialog, file not allowed
                                        Log.e("CreatePostFragment", "There was an error with the file selected.");
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
                                Log.d("CreatePostFragment", "postContent: " + postContent);
                                Log.d("CreatePostFragment", "postUri: " + selectedUri);
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
        LinearLayout rootLayout = rootView.findViewById(R.id.root_layout);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootLayout, 200f, isDarkMode);

        com.soundpaletteui.Views.EmojiBackgroundView emojiBg = rootView.findViewById(R.id.emojiBackground);
        emojiBg.setPatternType(EmojiBackgroundView.PATTERN_RADIAL);
        emojiBg.setAlpha(0.65f);
        user = AppSettings.getInstance().getUser();
        userId = user.getUserId();

        loadPostContent(rootView, postType);

        postCaption = rootView.findViewById(R.id.caption);
        isMatureCheckbox = rootView.findViewById(R.id.isMature);
        followerOnlyCheckbox = rootView.findViewById(R.id.isFollowing);

        // SET UP POST TAGS
        tagSearchInput = rootView.findViewById(R.id.tagSearchInput);
        tagSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)             {
                if(s.length()>3){
                    searchTagsAsync(s.toString());
                }
                else
                    new GetTagsAsync().execute();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        postTagSearchResult = rootView.findViewById(R.id.postTagSearchResult);
        selectedPostTags = rootView.findViewById(R.id.selectedPostTags);
        new GetTagsAsync().execute();
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
        selectedPostTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        selectedPostTags.setAdapter(selectedTagsAdapter);

        // SET UP USER TAGS
        userSearchInput = rootView.findViewById(R.id.userSearchInput);
        userSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)             {
                if(s.length()>3){
                    searchUsersAsync(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        userSearchResult = rootView.findViewById(R.id.userSearchResult);
        selectedUserTags = rootView.findViewById(R.id.selectedUserTags);
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
        selectedUserTags.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        selectedUserTags.setAdapter(selectedUsersAdapter);

        // PREVIEW BUTTON
        previewButton = rootView.findViewById(R.id.previewButton);
        previewButton.setOnClickListener(v -> {
            getNewPostDetails(postType);
            PostModel newPost = new PostModel(0, caption, new ArrayList<>(selectedTags), postContentModel,
                    new Date(), user.getUsername(), postType, 0, 0, false,
                    false, fileId, new ArrayList<>(selectedUsers)
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
                boolean hasPermissions;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasPermissions = hasPermission(Manifest.permission.READ_MEDIA_IMAGES)
                            && hasPermission(Manifest.permission.READ_MEDIA_AUDIO);
                } else {
                    hasPermissions = hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (!hasPermissions) {
                    Toast.makeText(requireContext(), "Permission needed to access your media files.", Toast.LENGTH_SHORT).show();
                    requestMediaPermissions();
                    return;
                }

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
        postContentModel = new PostContentModel(postContent, backgroundColour, fontColour);
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
                postContentView.setBackgroundColor(Color.parseColor(backgroundColour));
                postText.setTextColor(Color.parseColor(fontColour));
            } catch (IllegalArgumentException e) {
                Log.e("CreatePostFragment", "Invalid colour hex: " + e.getMessage());
            }
            postContentModel = new PostContentModel(postContent, backgroundColour, fontColour);
            fragmentDisplay.addView(postContentView);
        } else if (postType == 3) {
            View postContentView = inflater.inflate(R.layout.adapter_posts_image, fragmentDisplay, false);

            // Get file name (so it can source it from drawables)
            try {
                ImageView postImageDisplay = postContentView.findViewById(R.id.postImageDisplay);
                Log.d("CreatePostFragment", "selectedUri: " + selectedUri);
                if(selectedUri != null) {
                    Glide.with(this).load(selectedUri).into(postImageDisplay);
                }
                int imageResource = getResources().getIdentifier(postContent, "drawable", getContext().getPackageName());
                Log.d("CreatePostFragment", "Going to load "+postContent+ "#" +imageResource);
                postImageDisplay.setImageResource(imageResource);
            } catch (Exception e) {
                Log.e("CreatePostFragment", "Failed to load image from URI: " + postContent, e);
            }
            postContentModel = new PostContentModel(postContent, null, null);
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
                userId, postType, caption, isPremium, isMature, new Date(), new Date(),
                new ArrayList<>(selectedTags), postContentModel, selectedUsers, fileId);
        String fileName = null;
        if(selectedUri != null) {
            fileName = getFileNameFromUri(selectedUri);
        }
        fileModel = new FileModel(AppSettings.getInstance().getUserId(), fileName, fileId);
        Log.d("CreatePostFragment", "caption: " + caption);
        Log.d("CreatePostFragment", "fileName: " + fileModel.getFileName());
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
            //Log.d("MAKE POST ASYNC", "user wants to make a new post");
            //try {
            //Log.d("MAKE POST ASYNC", "trying to make the new post");
            // make post
            if (postType != 1) {
                File file = FileUtils.uri2File(requireContext(), selectedUri, fileId, AppSettings.getInstance().getUserId());
                Log.d("CreatePostFragment", "CPF fileName: " + file.getName());
                PostUtils.uploadFilePost(file, d[0], fileModel, new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        Log.d("CreatePostFragment", "Post successful");
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.d("CreatePostFragment", "Post failed ", t);
                    }
                });
            } else {
                PostClient client = SPWebApiRepository.getInstance().getPostClient();
                try {
                    client.makePost(d[0]);
                } catch (IOException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Error making post", Toast.LENGTH_SHORT).show());
                    throw new RuntimeException(e);
                }
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
                        backgroundColour = hexCode;
                        defaultBackgroundColor = selectedColor;
                        backgroundColourDisplay.setBackgroundColor(defaultBackgroundColor);
                    } else {
                        fontColour = hexCode;
                        defaultFontColor = selectedColor;
                        fontColourDisplay.setBackgroundColor(defaultFontColor);
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }


    private void searchTagsAsync(String searchTerm) {
        new Thread(() -> {
            try {
                TagClient client = SPWebApiRepository.getInstance().getTagClient();
                tags = client.searchTags(searchTerm);
                requireActivity().runOnUiThread(() -> {
                    searchTags.clear();
                    searchTags.addAll(tags);
                    tagSearchAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    private void searchUsersAsync(String searchTerm) {
        new Thread(() -> {
            try {
                UserClient client = SPWebApiRepository.getInstance().getUserClient();
                List<String> results = client.searchUsersLite(searchTerm);

                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                Log.e("NewChatroomFragment", "Failed to load dummy users: " + e.getMessage());
            }
        }).start();
    }


    // Whether the user gave permission to access camera, media files etc.
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    // Function to request permissions if they have not been granted
    private void requestMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_AUDIO
                    },
                    MEDIA_PERMISSION_REQUEST_CODE
            );
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    LEGACY_STORAGE_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MEDIA_PERMISSION_REQUEST_CODE || requestCode == LEGACY_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission granted! Please tap again to select media.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission denied. You won't be able to select media files.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
