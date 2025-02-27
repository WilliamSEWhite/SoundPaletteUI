package com.soundpaletteui.Activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Posts.CreatePostFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Trending.SearchFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;
import com.soundpaletteui.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity managing the primary navigation among fragments.
 */
public class MainScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private int userId;
    private UserModel user;
    private UserClient userClient;
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;

    //Sets up components, layout, and initializes default fragment on creation.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = AppSettings.getInstance().getUser();

        initComponents();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        animateHeaderShadow();
        View toolbar = findViewById(R.id.toolbar);
        UISettings.applyFlippedBrightnessGradientBackground(toolbar, 120f);
        replaceFragment(homeFragment);
        setInitialHomeButtonColor();
        setupCustomBottomNav();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            binding.bottomNavigationView.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
            );
            View toolbarView = findViewById(R.id.toolbar);
            ColorStateList tint;
            float hue = 0;
            int selected = item.getItemId();

            if (selected == R.id.nav_home) {
                hue = 30f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue);
                replaceFragment(homeFragment);
            } else if (selected == R.id.nav_profile) {
                hue = 55f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue);
                replaceFragment(profileFragment);
            } else if (selected == R.id.nav_create) {
                selectPostType();
            } else if (selected == R.id.nav_msg) {
                hue = 240f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue);
                replaceFragment(new MessageFragment());
            } else if (selected == R.id.nav_search) {
                hue = 330f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue);
                replaceFragment(searchFragment);
            } else {
                hue = 30f;
            }

            tint = createColorStateList(toolbarView, hue);
            binding.bottomNavigationView.setItemIconTintList(tint);
            binding.bottomNavigationView.setItemTextColor(tint);
            updateBottomNavShadows(item.getItemId(), hue);
            return true;
        });
    }

    // Function for Post creation - type selection
    private void selectPostType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View addPost = getLayoutInflater().inflate(R.layout.post_type_select_dialog, null);
        builder.setView(addPost);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button textPostButton = (Button) dialog.findViewById(R.id.create_text);
        Button audioPostButton = (Button) dialog.findViewById(R.id.create_sound);
        Button imagePostButton = (Button) dialog.findViewById(R.id.create_image);

        // if button is clicked, close the custom dialog
        textPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createPost(1);
            }
        });
        audioPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createPost(2);

            }
        });
        imagePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createPost(3);

            }
        });
    }

    // Function to create a post
    private void createPost(int postType){
        CreatePostFragment createPostFragment = CreatePostFragment.newInstance(postType);
        replaceFragment(createPostFragment);
    }

    // Animates a "breathing" shadow effect on the header text.
    private void animateHeaderShadow() {
        final TextView titleCenter = findViewById(R.id.title_center);
        if (titleCenter == null) return;
        final int shadowColor = getResources().getColor(R.color.white);
        final float initialRadius = 2f;
        final float finalRadius = 5f;
        final float[] currentRadius = {initialRadius};
        final float[] currentDx = {2f};
        final float[] currentDy = {2f};
        Runnable updateShadow = () -> {
            titleCenter.setShadowLayer(currentRadius[0], currentDx[0], currentDy[0], shadowColor);
        };
        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(initialRadius, finalRadius);
        radiusAnimator.setDuration(120000);
        radiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        radiusAnimator.addUpdateListener(animation -> {
            currentRadius[0] = (float) animation.getAnimatedValue();
            updateShadow.run();
        });
        radiusAnimator.start();
        ValueAnimator offsetAnimator = ValueAnimator.ofFloat(0f, 1f);
        offsetAnimator.setDuration(120000);
        offsetAnimator.setRepeatCount(ValueAnimator.INFINITE);
        offsetAnimator.setInterpolator(new LinearInterpolator());
        offsetAnimator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            currentDx[0] = 2f + (float) Math.sin(2 * Math.PI * fraction) * 5f;
            currentDy[0] = 2f + (float) Math.cos(2 * Math.PI * fraction) * 5f;
            updateShadow.run();
        });
        offsetAnimator.start();
    }

    // Creates a ColorStateList for tinting bottom navigation items based on hue.
    private ColorStateList createColorStateList(View toolbarView, float hue) {
        int selectedColor = Color.HSVToColor(new float[]{hue, 1f, 1f});
        int defaultColor = ContextCompat.getColor(this, android.R.color.darker_gray);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] colors = new int[]{selectedColor, defaultColor};
        return new ColorStateList(states, colors);
    }

    // Sets the initial color for the Home button upon startup.
    private void setInitialHomeButtonColor() {
        ColorStateList homeButtonTint = createColorStateList(findViewById(R.id.toolbar), 30f);
        binding.bottomNavigationView.setItemIconTintList(homeButtonTint);
        binding.bottomNavigationView.setItemTextColor(homeButtonTint);
    }

    // Initializes Fragment Activities to replace Main Fragment
    private void initComponents() {
        // Get the Intent that started this activity
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();
    }

    // Updates the adapter list once user data is retrieved.
    private void populateView() {
        if (user != null) {
            userList.clear();
            userList.add(user);
            mainContentAdapter.notifyDataSetChanged();
        }
    }

    // Replaces the main Fragment based on bottom navigation selection
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainScreenFrame, fragment);
        fragmentTransaction.commit();
    }

    //Sets up custom layouts for bottom navigation items.
    private void setupCustomBottomNav() {
        BottomNavigationView bottomNav = binding.bottomNavigationView;
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            MenuItem item = bottomNav.getMenu().getItem(i);
            View customView = LayoutInflater.from(this)
                    .inflate(R.layout.bottom_nav_item, bottomNav, false);
            ImageView icon = customView.findViewById(R.id.bottom_nav_icon);
            TextView label = customView.findViewById(R.id.bottom_nav_label);
            icon.setImageDrawable(item.getIcon());
            label.setText(item.getTitle());
            item.setActionView(customView);
        }
    }

    // Updates the shadow effect on the selected bottom navigation item.
    private void updateBottomNavShadows(int checkedItemId, float hue) {
        BottomNavigationView bottomNav = binding.bottomNavigationView;
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            MenuItem menuItem = bottomNav.getMenu().getItem(i);
            View actionView = menuItem.getActionView();
            if (actionView != null) {
                TextView label = actionView.findViewById(R.id.bottom_nav_label);
                label.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                label.setTypeface(null, Typeface.NORMAL);
            }
        }
        MenuItem checkedItem = bottomNav.getMenu().findItem(checkedItemId);
        if (checkedItem != null && checkedItem.getActionView() != null) {
            TextView label = checkedItem.getActionView().findViewById(R.id.bottom_nav_label);
            int selectedColor = Color.HSVToColor(new float[]{hue, 1f, 1f});
            int shadowColor = darkenColor(selectedColor);
            label.setShadowLayer(8, 6, 6, shadowColor);
            label.setTypeface(null, Typeface.BOLD);
        }
    }

    // Produces a darker color based on the provided color by reducing brightness.
    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}


