package com.soundpaletteui.Activities.Home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.MediaPlayerManager;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

// Represents the Home screen fragment for exploring and following content.
public class HomeFragment extends Fragment {
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private String userId;
    private UserModel user;
    private UserClient userClient;
    private View frameExplore;
    private GifImageView gifExplore;
    private TextView textExplore;
    private View frameFollower;
    private GifImageView gifFollower;
    private TextView textFollower;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private final int ORANGE_COLOR = Color.parseColor("#FFA500");
    private final int PINK_COLOR = Color.parseColor("#FFC0CB");
    private final int TRANSPARENT_ALPHA = 77;
    private final int FULL_ALPHA = 255;
    public boolean darkMode;
    private String selectedTab = "explore";

    // Listener for dark mode preference changes
    private SharedPreferences.OnSharedPreferenceChangeListener darkModeListener =
            (sharedPreferences, key) -> {
                if ("dark_mode_enabled".equals(key)) {
                    updateTabUI();
                }
            };

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the dark mode listener
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(darkModeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.unregisterOnSharedPreferenceChangeListener(darkModeListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(rootView);
        userId = String.valueOf(user.getUserId());
        com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView emojiBackground = rootView.findViewById(R.id.emojiBackground);

        // Sets the background gradient colour
        final View rootLayout = rootView.findViewById(R.id.root_layout);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(rootLayout.getContext());
        darkMode = isDarkMode;
        UISettings.applyBrightnessGradientBackground(rootLayout, 120f, isDarkMode);

        // Get the views
        frameExplore = rootView.findViewById(R.id.frame_explore);
        gifExplore = rootView.findViewById(R.id.gif_explore);
        textExplore = rootView.findViewById(R.id.explore_text);
        frameFollower = rootView.findViewById(R.id.frame_follower);
        gifFollower = rootView.findViewById(R.id.gif_follower);
        textFollower = rootView.findViewById(R.id.follower_text);

        // Explore button listener
        // Displays a mix of posts: 6 from followed users and 4 based on the user's preferred tags
        frameExplore.setOnClickListener(v -> {
            emojiBackground.setPatternType(com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView.PATTERN_SPIRAL);

            selectedTab = "explore";
            Log.d("HomeFragment", "Explore Selected");
            replacePostFragment("popular", null);
            try {
                final GifDrawable exploreGifDrawable = (GifDrawable) gifExplore.getDrawable();
                frameExplore.getBackground().mutate().setTint(ORANGE_COLOR);
                frameExplore.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameFollower.getBackground().mutate().setTint(ORANGE_COLOR);
                frameFollower.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);

                UISettings.applyBrightnessGradientBackground(rootLayout, 0f, darkMode);
                exploreGifDrawable.start();
                gifHandler.postDelayed(exploreGifDrawable::stop, 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Explore GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifFollower.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifFollower.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifExplore.setAlpha(1.0f);
            gifFollower.setAlpha(0.3f);
            setButtonTextSelected(textExplore, true);
            setButtonTextSelected(textFollower, false);

            View toolbar = requireActivity().findViewById(R.id.toolbar);
            UISettings.applyFlippedBrightnessGradientBackground(toolbar, 5f, isDarkMode);
        });

        // Follower button listener
        // Show posts exclusively from followed users
        frameFollower.setOnClickListener(v -> {
            emojiBackground.setPatternType(EmojiBackgroundView.PATTERN_RADIAL);

            selectedTab = "following";
            Log.d("HomeFragment", "Followers Selected for UserID #" + userId);
            replacePostFragment("following", userId);
            try {
                final GifDrawable followerGifDrawable = (GifDrawable) gifFollower.getDrawable();
                frameFollower.getBackground().mutate().setTint(PINK_COLOR);
                frameFollower.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameExplore.getBackground().mutate().setTint(PINK_COLOR);
                frameExplore.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);

                UISettings.applyBrightnessGradientBackground(rootLayout, 330f, darkMode);
                followerGifDrawable.start();
                gifHandler.postDelayed(followerGifDrawable::stop, 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Follower GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifExplore.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifExplore.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifFollower.setAlpha(1.0f);
            gifExplore.setAlpha(0.3f);
            setButtonTextSelected(textFollower, true);
            setButtonTextSelected(textExplore, false);

            View toolbar = requireActivity().findViewById(R.id.toolbar);
            UISettings.applyFlippedBrightnessGradientBackground(toolbar, 330f, isDarkMode);
        });

        // Default view is the "Explore" tab
        frameExplore.performClick();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabUI();
    }

    // Updates the backgrounds and toolbar based on the selected tab and current dark mode setting.
    private void updateTabUI() {
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(getContext());
        darkMode = isDarkMode;
        View rootLayout = getView().findViewById(R.id.root_layout);
        View toolbar = requireActivity().findViewById(R.id.toolbar);
        if ("explore".equals(selectedTab)) {
            UISettings.applyBrightnessGradientBackground(rootLayout, 0f, isDarkMode);
            UISettings.applyFlippedBrightnessGradientBackground(toolbar, 0f, isDarkMode);
        } else {
            UISettings.applyBrightnessGradientBackground(rootLayout, 330f, isDarkMode);
            UISettings.applyFlippedBrightnessGradientBackground(toolbar, 330f, isDarkMode);
        }
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    // Set Dark-mode
    public void setTheme(boolean darkM){
        updateTabUI();
    }

    // Initializes views and loads user data.
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
    }

    // Replaces the Post Fragment (Fragment in  Fragment)
    private void replacePostFragment(String algoType, String userId) {
        PostFragment postFragment = PostFragment.newInstance(algoType, userId, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Navigation.replaceFragment(requireActivity().getSupportFragmentManager(),
                transaction, postFragment,
                "POST_FRAGMENT",
                R.id.postFragment);
    }

    // Sets the text style for a TextView based on whether it is selected.
    private void setButtonTextSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
//            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(20);
        } else {
//            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextSize(18);
        }
    }

    // Pauses the media player when user leaves the fragment
    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerManager.getInstance().release();
    }
}
