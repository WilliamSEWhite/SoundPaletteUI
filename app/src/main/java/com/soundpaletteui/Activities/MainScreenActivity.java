package com.soundpaletteui.Activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar; // Make sure to import Toolbar
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Posts.CreatePostFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Trending.SearchFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity managing the primary navigation among fragments.
 */
public class MainScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private UserModel user;
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply dark mode setting before setting content view.
        if (DarkModePreferences.isDarkModeEnabled(this)) {
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setTitle("");
        user = AppSettings.getInstance().getUser();
        initComponents();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        animateHeaderShadow();

        Navigation.replaceFragment(getSupportFragmentManager(), homeFragment, "HOME_FRAGMENT", R.id.mainScreenFrame);
        setInitialHomeButtonColor();
        // Invalidate the menu so the three dots become visible (since HomeFragment is active)
        supportInvalidateOptionsMenu();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(this);

            if (isDarkMode) {
                binding.bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.background_dark));
            } else {
                binding.bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            }

            View toolbarView = findViewById(R.id.toolbar);
            ColorStateList tint;
            float hue = 30;
            int selected = item.getItemId();

            if (selected == R.id.nav_home) {
                hue = 0f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), homeFragment, "HOME_FRAGMENT", R.id.mainScreenFrame);
            } else if (selected == R.id.nav_profile) {
                hue = 55f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), profileFragment, "PROFILE_FRAGMENT", R.id.mainScreenFrame);
            } else if (selected == R.id.nav_create) {
                selectPostType();
            } else if (selected == R.id.nav_msg) {
                hue = 240f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), new MessageFragment(), "MESSAGE_FRAGMENT", R.id.mainScreenFrame);
            } else if (selected == R.id.nav_search) {
                hue = 280f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), searchFragment, "SEARCH_FRAGMENT", R.id.mainScreenFrame);
            } else {
                hue = 30f;
            }
animateHeaderShadow();
            tint = createColorStateList(toolbarView, hue);
            binding.bottomNavigationView.setItemIconTintList(tint);
            binding.bottomNavigationView.setItemTextColor(tint);
            updateBottomNavShadows(item.getItemId(), hue);
            supportInvalidateOptionsMenu();
            return true;
        });
    }

    public void viewPostsByTags(String tagId){
        searchFragment.viewPostsByTag(tagId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_header, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainScreenFrame);
        MenuItem darkModeToggle = menu.findItem(R.id.action_toggle_dark_mode);
        if (darkModeToggle != null) {
            if (currentFragment instanceof HomeFragment) {
                darkModeToggle.setVisible(true);
            } else {
                darkModeToggle.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // Handle menu item clicks, including the dark mode toggle.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle_dark_mode) {
            System.out.println("Button change");
            UISettings.applyFlippedBrightnessGradientBackground(recyclerView, 120f, homeFragment.darkMode);

            // Toggle the dark mode preference.
            boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(this);
            DarkModePreferences.setDarkModeEnabled(this, !isDarkMode);
            System.out.println("DARK MODE: " + isDarkMode);
            if (!isDarkMode) {
                binding.bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.background_dark));

            } else {
                binding.bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            }
//            homeFragment.setTheme(isDarkMode);
            recreate();
            return true;
        }
        else if(id == R.id.log_out){
            AppSettings.setUsernameValue(this, "");
            AppSettings.setPasswordValue(this, "");

            Intent intent = new Intent(MainScreenActivity.this, LoginActivity.class);
            startActivity(intent); // Start the next activity
            finish(); // Finish the current activity

        }
        return super.onOptionsItemSelected(item);
    }

    // Initializes Fragment Activities to replace Main Fragment.
    private void initComponents() {
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();
    }

    // Function for Post creation - type selection.
    private void selectPostType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View addPost = getLayoutInflater().inflate(R.layout.post_type_select_dialog, null);
        builder.setView(addPost);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button textPostButton = dialog.findViewById(R.id.create_text);
        Button mediaPostButton = dialog.findViewById(R.id.createMedia);

        textPostButton.setOnClickListener(v -> {
            dialog.dismiss();
            CreatePostFragment createPostFragment = CreatePostFragment.newInstance(1);
            Navigation.replaceFragment(getSupportFragmentManager(), createPostFragment, "CREATE_POST_FRAGMENT_TEXT", R.id.mainScreenFrame);
            supportInvalidateOptionsMenu();
        });
        mediaPostButton.setOnClickListener(v -> {
            dialog.dismiss();
            CreatePostFragment createPostFragment = CreatePostFragment.newInstance(-1);
            Navigation.replaceFragment(getSupportFragmentManager(), createPostFragment, "CREATE_POST_FRAGMENT_FILE", R.id.mainScreenFrame);
            supportInvalidateOptionsMenu();
        });
    }

    // Animates a "breathing" shadow effect on the header text.
    private void animateHeaderShadow() {
        final TextView titleCenter = findViewById(R.id.title_center);
        if (titleCenter == null) return;
        final int shadowColor = getResources().getColor(R.color.colorBackgroundLight);
        final float initialRadius = 2f;
        final float finalRadius = 5f;
        final float[] currentRadius = {initialRadius};
        final float[] currentDx = {2f};
        final float[] currentDy = {2f};
        Runnable updateShadow = () -> titleCenter.setShadowLayer(currentRadius[0], currentDx[0], currentDy[0], shadowColor);
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
        ColorStateList homeButtonTint = createColorStateList(findViewById(R.id.toolbar), 0f);
        binding.bottomNavigationView.setItemIconTintList(homeButtonTint);
        binding.bottomNavigationView.setItemTextColor(homeButtonTint);

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
//                label.setTypeface(null, Typeface.NORMAL);
                label.setTextSize(10);

            }
        }
        MenuItem checkedItem = bottomNav.getMenu().findItem(checkedItemId);
        if (checkedItem != null && checkedItem.getActionView() != null) {
            TextView label = checkedItem.getActionView().findViewById(R.id.bottom_nav_label);
            int selectedColor = Color.HSVToColor(new float[]{hue, 1f, 1f});
            int shadowColor = darkenColor(selectedColor);
            label.setShadowLayer(8, 6, 6, shadowColor);
//            label.setTypeface(null, Typeface.BOLD);
            label.setTextSize(12);
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
