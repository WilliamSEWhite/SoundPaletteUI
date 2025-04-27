// SearchFragment.java
// Displays the search screen where users can browse trending posts, tags, and users, and perform searches.

package com.soundpaletteui.Activities.Trending;

// (Static imports for view visibility constants)
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Adapters.CountrySelectAdapter;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SearchFragment extends Fragment {

    // RecyclerView and adapters
    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;

    // Current user and client
    private UserModel user;
    private UserClient userClient;

    // Search bar and selected tab tracking
    private EditText inputSearch;
    private String selectedTab = "posts";

    // Views for tabs
    private View framePosts;
    private GifImageView gifPosts;
    private TextView textPosts;
    private View frameTags;
    private GifImageView gifTags;
    private TextView textTags;
    private View frameUsers;
    private GifImageView gifUsers;
    private TextView textUsers;

    // Alpha values for visual effect
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;

    // Trending time range selection
    private LinearLayout trendingRange;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private Spinner trendingSpinner;
    String range = "Past Week";  // Default trending range

    public SearchFragment() {}

    // Create a new instance of SearchFragment
    public static SearchFragment newInstance(int userId) {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Apply background settings
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 330f, isDarkMode);

        final View rootLayout = rootView.findViewById(R.id.root_layout);

        EmojiBackgroundView emojiBackground = rootView.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBackground.setAlpha(0.65f);

        // Set up all views
        initComponents(rootView);

        // Set up frames for tabs
        framePosts = rootView.findViewById(R.id.frame_posts);
        gifPosts = rootView.findViewById(R.id.gif_posts);
        textPosts = rootView.findViewById(R.id.postsToggle);

        frameTags = rootView.findViewById(R.id.frame_tags);
        gifTags = rootView.findViewById(R.id.gif_tags);
        textTags = rootView.findViewById(R.id.tagsToggle);

        frameUsers = rootView.findViewById(R.id.frame_users);
        gifUsers = rootView.findViewById(R.id.gif_users);
        textUsers = rootView.findViewById(R.id.usersToggle);

        trendingRange = rootView.findViewById(R.id.trending_range_select);
        trendingSpinner = rootView.findViewById(R.id.trending_range);

        inputSearch = rootView.findViewById(R.id.inputSearch);
        ImageButton buttonSearch = rootView.findViewById(R.id.buttonSearch);

        // Set up click listeners for each tab
        setupTabListeners(rootView, emojiBackground, rootLayout, isDarkMode);

        // Set up the trending range spinner
        setupTrendingSpinner();

        // Set up the search button
        buttonSearch.setOnClickListener(v -> {
            switch(selectedTab){
                case "posts":
                    setPostFragment();
                    break;
                case "tags":
                    setTagsFragment();
                    break;
                case "users":
                    setUsersFragment();
                    break;
            }
        });

        // When the search input changes, reload posts if needed
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1 && Objects.equals(selectedTab, "posts")) {
                    setPostFragment();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Default to clicking posts first
        framePosts.performClick();

        return rootView;
    }

    // Set the text style depending on if a tab is selected
    private void setButtonTextSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setTextSize(20);
        } else {
            textView.setTextSize(18);
        }
    }

    // Initialize basic components
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
    }

    // Set up the listeners for tab buttons
    private void setupTabListeners(View rootView, EmojiBackgroundView emojiBackground, View rootLayout, boolean isDarkMode) {
        framePosts.setOnClickListener(v -> {
            handleTabSwitch("posts", emojiBackground, rootLayout, isDarkMode);
            setPostFragment();
        });

        frameTags.setOnClickListener(v -> {
            trendingRange.setVisibility(GONE);
            handleTabSwitch("tags", emojiBackground, rootLayout, isDarkMode);
            setTagsFragment();
        });

        frameUsers.setOnClickListener(v -> {
            trendingRange.setVisibility(GONE);
            handleTabSwitch("users", emojiBackground, rootLayout, isDarkMode);
            setUsersFragment();
        });
    }

    // Handles switching between tabs visually and logically
    private void handleTabSwitch(String newTab, EmojiBackgroundView emojiBackground, View rootLayout, boolean isDarkMode) {
        selectedTab = newTab;
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);

        // Reset all alphas
        framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
        frameTags.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
        frameUsers.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);

        // Highlight selected frame
        switch (newTab) {
            case "posts":
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                playGif(gifPosts);
                stopGif(gifTags);
                stopGif(gifUsers);
                break;
            case "tags":
                frameTags.getBackground().mutate().setAlpha(FULL_ALPHA);
                playGif(gifTags);
                stopGif(gifPosts);
                stopGif(gifUsers);
                break;
            case "users":
                frameUsers.getBackground().mutate().setAlpha(FULL_ALPHA);
                playGif(gifUsers);
                stopGif(gifTags);
                stopGif(gifPosts);
                break;
        }

        // Highlight selected text
        setButtonTextSelected(textPosts, newTab.equals("posts"));
        setButtonTextSelected(textTags, newTab.equals("tags"));
        setButtonTextSelected(textUsers, newTab.equals("users"));

        // Reapply background gradient
        UISettings.applyBrightnessGradientBackground(rootLayout, 330f, isDarkMode);
    }

    // Plays a GIF animation
    private void playGif(GifImageView gif) {
        try {
            final GifDrawable gifDrawable = (GifDrawable) gif.getDrawable();
            gifDrawable.start();
            gifHandler.postDelayed(gifDrawable::stop, 800);
            gif.setAlpha(1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Stops a GIF animation
    private void stopGif(GifImageView gif) {
        try {
            if (gif.getDrawable() instanceof GifDrawable) {
                ((GifDrawable) gif.getDrawable()).stop();
                gif.setAlpha(0.3f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sets up the trending spinner
    private void setupTrendingSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.trending_ranges,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trendingSpinner.setAdapter(adapter);
        trendingSpinner.setSelection(0);

        trendingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object selectedRange = parent.getItemAtPosition(pos);
                range = (String) selectedRange;
                setPostFragment();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Loads the PostFragment based on search or trending
    private void setPostFragment() {
        String algoType;
        String searchTerm = inputSearch.getText().toString();

        if (searchTerm.isEmpty()) {
            algoType = "trending";
            trendingRange.setVisibility(VISIBLE);
            searchTerm = range;
        } else {
            algoType = "search";
            trendingRange.setVisibility(GONE);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, searchTerm);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    // Loads the Tags search fragment
    private void setTagsFragment() {
        String searchTerm = inputSearch.getText().toString();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        SearchTagsFragment searchTagsFragment = SearchTagsFragment.newInstance(searchTerm);
        transaction.replace(R.id.postFragment, searchTagsFragment);
        transaction.commit();
    }

    // View posts related to a tag
    public void viewPostsByTag(String tagId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance("tag", tagId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    // Loads the Users search fragment
    private void setUsersFragment() {
        String searchTerm = inputSearch.getText().toString();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        SearchProfileFragment searchProfileFragment = SearchProfileFragment.newInstance(searchTerm);
        transaction.replace(R.id.postFragment, searchProfileFragment);
        transaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Release any media playing when leaving the fragment
        MediaPlayerManager.getInstance().release();
    }
}
