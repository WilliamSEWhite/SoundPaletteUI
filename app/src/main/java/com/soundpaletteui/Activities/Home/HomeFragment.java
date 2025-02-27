package com.soundpaletteui.Activities.Home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soundpaletteui.UISettings;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Represents the Home screen fragment for exploring and following content.
 */
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

    public HomeFragment() {
    }

    // Initializes the fragment with the arguments passed in.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflates the layout and sets up UI elements for the fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(rootView);
        userId = String.valueOf(user.getUserId());

        final View rootLayout = rootView.findViewById(R.id.root_layout);
        UISettings.applyBrightnessGradientBackground(rootLayout, 120f);
        initComponents(rootView);

        frameExplore = rootView.findViewById(R.id.frame_explore);
        gifExplore = rootView.findViewById(R.id.gif_explore);
        textExplore = rootView.findViewById(R.id.explore_text);
        frameFollower = rootView.findViewById(R.id.frame_follower);
        gifFollower = rootView.findViewById(R.id.gif_follower);
        textFollower = rootView.findViewById(R.id.follower_text);

        frameExplore.setOnClickListener(v -> {
            try {
                final GifDrawable exploreGifDrawable = (GifDrawable) gifExplore.getDrawable();
                frameExplore.getBackground().mutate().setTint(ORANGE_COLOR);
                frameExplore.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameFollower.getBackground().mutate().setTint(ORANGE_COLOR);
                frameFollower.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                UISettings.applyBrightnessGradientBackground(rootLayout, 30f);
                exploreGifDrawable.start();
                gifHandler.postDelayed(() -> exploreGifDrawable.stop(), 800);
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

            Log.d("HomeFragment", "Explore Selected");
            replaceFragment("popular", null);
            View toolbar = requireActivity().findViewById(R.id.toolbar);
            UISettings.applyFlippedBrightnessGradientBackground(toolbar, 30f);
        });

        frameFollower.setOnClickListener(v -> {
            try {
                final GifDrawable followerGifDrawable = (GifDrawable) gifFollower.getDrawable();
                frameFollower.getBackground().mutate().setTint(PINK_COLOR);
                frameFollower.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameExplore.getBackground().mutate().setTint(PINK_COLOR);
                frameExplore.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                UISettings.applyBrightnessGradientBackground(rootLayout, 330f);
                followerGifDrawable.start();
                gifHandler.postDelayed(() -> followerGifDrawable.stop(), 800);
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

            Log.d("HomeFragment", "Followers Selected for UserID #" + userId);
            replaceFragment("followers", userId);
            View toolbar = requireActivity().findViewById(R.id.toolbar);
            UISettings.applyFlippedBrightnessGradientBackground(toolbar, 330f);
        });

        frameExplore.performClick();
        return rootView;
    }

    // Initializes views and loads user data.
    private void initComponents(View view) {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
    }

    // Replaces the PostFragment based on the algorithmType and userId
    private void replaceFragment(String algoType, String userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(algoType, userId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    //Sets the text style for a TextView based on whether it is selected.
    private void setButtonTextSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(19);
        } else {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextSize(18);
        }
    }
}
