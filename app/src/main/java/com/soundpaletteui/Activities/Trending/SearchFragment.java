package com.soundpaletteui.Activities.Trending;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.soundpaletteui.Views.EmojiBackgroundView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private UserModel user;
    private UserClient userClient;
    private EditText inputSearch;
    private String selectedTab = "posts";
    private View framePosts;
    private GifImageView gifPosts;
    private TextView textPosts;
    private View frameTags;
    private GifImageView gifTags;
    private TextView textTags;
    private View frameUsers;
    private GifImageView gifUsers;
    private TextView textUsers;
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;
    private LinearLayout trendingRange;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private Spinner trendingSpinner;
    String range = "Past Week";


    public SearchFragment() {
    }

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
        // Inflate layout wrapped in a FrameLayout with EmojiBackgroundView
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootView.getContext());
        UISettings.applyBrightnessGradientBackground(rootView, 305f, isDarkMode);
        final View rootLayout = rootView.findViewById(R.id.root_layout);

        EmojiBackgroundView emojiBackground = rootView.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_SPIRAL);
        emojiBackground.setAlpha(0.65f);

        initComponents(rootView);

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

        framePosts.setOnClickListener(v -> {

            emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);

            selectedTab = "posts";
            try {
                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameTags.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                frameUsers.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);

                postsGifDrawable.start();
                gifHandler.postDelayed(() -> postsGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Posts GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifTags.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifTags.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (gifUsers.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifUsers.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifPosts.setAlpha(1.0f);
            gifTags.setAlpha(0.3f);
            gifUsers.setAlpha(0.3f);

            setButtonTextSelected(textPosts, true);
            setButtonTextSelected(textTags, false);
            setButtonTextSelected(textUsers, false);


            setPostFragment();
            UISettings.applyBrightnessGradientBackground(rootLayout, 305f, isDarkMode);
        });
        frameTags.setOnClickListener(v -> {
            trendingRange.setVisibility(GONE);

            emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);

            selectedTab = "tags";
            try {
                final GifDrawable tagsGifDrawable = (GifDrawable) gifTags.getDrawable();
                frameTags.getBackground().mutate().setAlpha(FULL_ALPHA);
                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                frameUsers.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);

                tagsGifDrawable.start();
                gifHandler.postDelayed(() -> tagsGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Tagsd GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifPosts.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifPosts.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (gifUsers.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifUsers.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifTags.setAlpha(1.0f);
            gifPosts.setAlpha(0.3f);
            gifUsers.setAlpha(0.3f);

            setButtonTextSelected(textTags, true);
            setButtonTextSelected(textPosts, false);
            setButtonTextSelected(textUsers, false);


            setTagsFragment();
            UISettings.applyBrightnessGradientBackground(rootLayout, 305f, isDarkMode);
        });
        frameUsers.setOnClickListener(v -> {
            trendingRange.setVisibility(GONE);

            emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_GRID);

            selectedTab = "users";
            try {
                final GifDrawable usersGifDrawable = (GifDrawable) gifUsers.getDrawable();
                frameUsers.getBackground().mutate().setAlpha(FULL_ALPHA);
                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                frameTags.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);

                usersGifDrawable.start();
                gifHandler.postDelayed(() -> usersGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting users GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifTags.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifTags.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (gifPosts.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifPosts.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifUsers.setAlpha(1.0f);
            gifPosts.setAlpha(0.3f);
            gifTags.setAlpha(0.3f);

            setButtonTextSelected(textUsers, true);
            setButtonTextSelected(textPosts, false);
            setButtonTextSelected(textTags, false);


            setUsersFragment();
            UISettings.applyBrightnessGradientBackground(rootLayout, 315f, isDarkMode);
        });
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)             {
                if(s.length()<1 && Objects.equals(selectedTab, "posts")){
                    setPostFragment();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this.getContext(),
                R.array.trending_ranges,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trendingSpinner.setAdapter(adapter);
        trendingSpinner.setSelection(0);

        trendingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                Object selectedRange = parent.getItemAtPosition(pos);
                String range = (String) selectedRange;
                setPostFragment();
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        framePosts.performClick();


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

        return rootView;
    }
    private void setButtonTextSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
//            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(20);
        } else {
//            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextSize(18);
        }
    }
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
    }

    private void setPostFragment(){
        String algoType;
        String searchTerm = inputSearch.getText().toString();

        if(searchTerm.isEmpty()){
            algoType = "trending";
            trendingRange.setVisibility(VISIBLE);
            searchTerm = range;
        }
        else{
            algoType = "search";
            trendingRange.setVisibility(GONE);
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, searchTerm);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }
    private void setTagsFragment(){
        String searchTerm = inputSearch.getText().toString();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        SearchTagsFragment searchTagsFragment = SearchTagsFragment.newInstance(searchTerm);
        transaction.replace(R.id.postFragment, searchTagsFragment);
        transaction.commit();

    }

    public void viewPostsByTag(String tagId){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance("tag", String.valueOf(tagId));
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    private void setUsersFragment(){
        String searchTerm = inputSearch.getText().toString();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        SearchProfileFragment searchProfileFragment = SearchProfileFragment.newInstance(searchTerm);
        transaction.replace(R.id.postFragment, searchProfileFragment);
        transaction.commit();
    }
    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }
}
