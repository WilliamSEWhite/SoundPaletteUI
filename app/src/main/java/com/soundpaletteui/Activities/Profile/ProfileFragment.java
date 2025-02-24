package com.soundpaletteui.Activities.Profile;

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
 * Displays and manages a user's profile, including posts and saved content.
 */
public class ProfileFragment extends Fragment {

    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;
    private View framePosts;
    private GifImageView gifPosts;
    private TextView textPosts;
    private View frameSaved;
    private GifImageView gifSaved;
    private TextView textSaved;
    private Handler gifHandler = new Handler(Looper.getMainLooper());
    private final int FULL_ALPHA = 255;
    private final int TRANSPARENT_ALPHA = 77;

    /**
     * Default constructor for ProfileFragment.
     */
    public ProfileFragment() {
    }

    /**
     * Returns a new instance of ProfileFragment with the specified userId.
     */
    public static ProfileFragment newInstance(int userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the fragment with any arguments provided.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
    }

    /**
     * Inflates the layout and sets up UI for posts and saved content.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        UISettings.applyBrightnessGradientBackground(rootView, 50f);
        framePosts = rootView.findViewById(R.id.frame_posts);
        gifPosts = rootView.findViewById(R.id.gif_posts);
        textPosts = rootView.findViewById(R.id.posts_text);
        frameSaved = rootView.findViewById(R.id.frame_saved);
        gifSaved = rootView.findViewById(R.id.gif_saved);
        textSaved = rootView.findViewById(R.id.saved_text);
        initComponents(rootView);
        Random random = new Random();

        framePosts.setOnClickListener(v -> {
            try {
                final GifDrawable postsGifDrawable = (GifDrawable) gifPosts.getDrawable();
                framePosts.getBackground().mutate().setAlpha(FULL_ALPHA);
                frameSaved.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                UISettings.applyBrightnessGradientBackground(rootView, 50f);
                postsGifDrawable.start();
                gifHandler.postDelayed(() -> postsGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Posts GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifSaved.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifSaved.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifPosts.setAlpha(1.0f);
            gifSaved.setAlpha(0.3f);
            setButtonTextSelected(textPosts, true);
            setButtonTextSelected(textSaved, false);
            int randomPostsNumber = random.nextInt(6) + 10;
            Log.d("ProfileFragment", "Posts clicked - randomNumber: " + randomPostsNumber);
            replaceFragment(randomPostsNumber);
        });

        frameSaved.setOnClickListener(v -> {
            try {
                final GifDrawable savedGifDrawable = (GifDrawable) gifSaved.getDrawable();
                frameSaved.getBackground().mutate().setAlpha(FULL_ALPHA);
                framePosts.getBackground().mutate().setAlpha(TRANSPARENT_ALPHA);
                UISettings.applyBrightnessGradientBackground(rootView, 60f);
                savedGifDrawable.start();
                gifHandler.postDelayed(() -> savedGifDrawable.stop(), 800);
            } catch (ClassCastException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(),
                        "Error casting Saved GIF to GifDrawable",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                if (gifPosts.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) gifPosts.getDrawable()).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            gifSaved.setAlpha(1.0f);
            gifPosts.setAlpha(0.3f);
            setButtonTextSelected(textSaved, true);
            setButtonTextSelected(textPosts, false);
            int randomSavedNumber = random.nextInt(9) + 1;
            Log.d("ProfileFragment", "Saved clicked - randomNumber: " + randomSavedNumber);
            replaceFragment(randomSavedNumber);
        });

        framePosts.performClick();
        return rootView;
    }

    /**
     * Initializes components and loads the user data.
     */
    private void initComponents(View view) {
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
        getUser();
    }

    /**
     * Retrieves user data in a background thread.
     */
    private void getUser() {
        new Thread(() -> {
            try {
                user = userClient.getUser(userId);
                requireActivity().runOnUiThread(this::populateView);
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                "Error fetching user",
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Populates the view once the user data is loaded.
     */
    private void populateView() {
        userList.clear();
        userList.add(user);
        if (mainContentAdapter != null) {
            mainContentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Replaces the current child fragment with a PostFragment for a given userId.
     */
    private void replaceFragment(int userId) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PostFragment postFragment = PostFragment.newInstance(userId);
        transaction.replace(R.id.postFragment, postFragment);
        transaction.commit();
    }

    /**
     * Sets the style of a TextView to selected or unselected.
     */
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
