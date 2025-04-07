package com.soundpaletteui.Activities.Posts;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.soundpaletteui.Activities.Home.HomeFragment;
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
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostTextFragment extends Fragment {

    private int postType;
    private UserModel user;
    private List<TagModel> tags;
    private int userId;
    private List<UserProfileModelLite> selectedUsers = new ArrayList<>();
    private List<UserProfileModelLite> searchResults = new ArrayList<>();
    private List<TagModel> selectedTags = new ArrayList<>();
    private List<TagModel> searchTags = new ArrayList<>();
    private TagSearchAdapter tagSearchAdapter;
    private TagSelectedAdapter selectedTagsAdapter;
    private UserSearchAdapter userSearchAdapter;
    private UserSelectedAdapter selectedUsersAdapter;
    private CheckBox isMatureCheckbox, followerOnlyCheckbox;
    private View backgroundColourDisplay, fontColourDisplay;
    private EditText postCaption, postTextContext, tagSearchInput, userSearchInput;
    private RecyclerView postTagSearchResult, selectedPostTags, userSearchResult, selectedUserTags;
    private Button previewButton, postButton;
    private MaterialButton backgroundColourSelector, fontColourSelector;
    private int defaultBackgroundColor = 0xFFFFFFFF;
    private int defaultFontColor = 0xFF000000;
    private String backgroundHex = "#FFFFFF";
    private String fontHex = "#000000";
    private boolean selectingBackground = true;
    String caption, textContent;
    boolean isPremium, isMature;

    public CreatePostTextFragment() {}

    public static CreatePostTextFragment newInstance(int postType) {
        CreatePostTextFragment fragment = new CreatePostTextFragment();
        Bundle args = new Bundle();
        args.putInt("Post_Type", postType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postType = getArguments().getInt("Post_Type", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_create_text, container, false);
        user = AppSettings.getInstance().getUser();
        userId = user.getUserId();

        postCaption = rootView.findViewById(R.id.caption);
        postTextContext = rootView.findViewById(R.id.textContent);
        isMatureCheckbox = rootView.findViewById(R.id.isMature);
        followerOnlyCheckbox = rootView.findViewById(R.id.isFollowing);
        backgroundColourDisplay = rootView.findViewById(R.id.backgroundColourDisplay);
        fontColourDisplay = rootView.findViewById(R.id.fontColourDisplay);
        backgroundColourSelector = rootView.findViewById(R.id.backgroundColourSelector);
        fontColourSelector = rootView.findViewById(R.id.fontColourSelector);

        backgroundColourSelector.setOnClickListener(v -> openColourPicker(true));
        fontColourSelector.setOnClickListener(v -> openColourPicker(false));

        tagSearchInput = rootView.findViewById(R.id.tagSearchInput);
        postTagSearchResult = rootView.findViewById(R.id.postTagSearchResult);
        selectedPostTags = rootView.findViewById(R.id.selectedPostTags);
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
        new GetTagsAsync().execute();

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

        previewButton = rootView.findViewById(R.id.previewButton);
        previewButton.setOnClickListener(v -> {
            getNewPostDetails();
            PostContentModel content = new PostContentModel(textContent);
            PostModel newPost = new PostModel(0, caption, new ArrayList<>(selectedTags), content, new Date(), user.getUsername(), postType, 0, 0, false, false
            );

            showPostPreview(newPost);
        });


        postButton = rootView.findViewById(R.id.postButton);
        postButton.setOnClickListener(v -> {
            getNewPostDetails();
            savePost();
        });

        return rootView;
    }

    private void getNewPostDetails() {
        caption = postCaption.getText().toString();
        textContent = postTextContext.getText().toString();
        isMature = isMatureCheckbox.isChecked();
        isPremium = followerOnlyCheckbox.isChecked();
    }

    private void savePost() {
        NewPostModel newPost = new NewPostModel(userId, postType, caption, isPremium, isMature, new Date(), new Date(), new ArrayList<>(selectedTags), textContent);
        new MakePostAsync().execute(newPost);
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
        View postContentView = inflater.inflate(R.layout.adapter_posts_text, fragmentDisplay, false);

        // Set the username, caption and post context
        TextView username = previewView.findViewById(R.id.postUsername);
        TextView caption = previewView.findViewById(R.id.postCaption);
        TextView postText = postContentView.findViewById(R.id.postTextDisplay);
        username.setText(previewPost.getUsername());
        caption.setText(previewPost.getPostCaption());
        postText.setText(previewPost.getPostContent().getPostTextContent());

        // Setting the user's selected background and font colours
        // Note: When colours added to database, should feed colours as parameter into Post Models, and assign in PostAdapter
        try {
            postContentView.setBackgroundColor(Color.parseColor(backgroundHex));
            postText.setTextColor(Color.parseColor(fontHex));
        } catch (IllegalArgumentException e) {
            Log.e("PreviewError", "Invalid colour hex: " + e.getMessage());
        }

        fragmentDisplay.addView(postContentView);

        FrameLayout previewContainer = requireView().findViewById(R.id.postPreviewContainer);
        previewContainer.removeAllViews();
        previewContainer.addView(previewView);
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
            replaceMainFragment(new HomeFragment(), "HOME_FRAGMENT");
        }
    }

    private void replaceMainFragment(Fragment new_fragment, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Navigation.replaceFragment(fragmentManager, new_fragment, tag, R.id.mainScreenFrame);
    }

    private void populateDummyUsersAsync() {
        new Thread(() -> {
            try {
                UserClient client = SPWebApiRepository.getInstance().getUserClient();
                List<UserProfileModelLite> results = new ArrayList<>();
                results.add(client.getUserProfileByUsername("user1"));
                results.add(client.getUserProfileByUsername("user2"));
                results.add(client.getUserProfileByUsername("user3"));
                results.add(client.getUserProfileByUsername("user4"));
                requireActivity().runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(results);
                    userSearchAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                Log.e("NewTextPostFragment", "Failed to load dummy users: " + e.getMessage());
            }
        }).start();
    }
}
