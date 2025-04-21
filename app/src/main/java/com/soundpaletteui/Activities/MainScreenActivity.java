package com.soundpaletteui.Activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Notifications.NotificationFragment;
import com.soundpaletteui.Activities.Notifications.NotificationSettingsFragment;
import com.soundpaletteui.Activities.Posts.CreatePostFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Trending.SearchFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.NotificationClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Main activity managing the primary navigation among fragments.
public class MainScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private UserModel user;
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private Handler notificationPollingHandler = new Handler(Looper.getMainLooper());
    private Runnable notificationPollingRunnable;
    private boolean isPollingNotifications = false;
    private Handler messagePollingHandler = new Handler(Looper.getMainLooper());
    private Runnable messagePollingRunnable;
    private boolean isPollingMessages = false;
    private Handler deviceNotificationPollingHandler = new Handler(Looper.getMainLooper());
    private Runnable deviceNotificationPollingRunnable;
    private boolean isPollingDeviceNotifications = false;
    private final NotificationClient notificationClient = SPWebApiRepository.getInstance().getNotificationClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply dark mode setting before setting content view.
        if (!DarkModePreferences.isDarkModeEnabled(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setTitle("");
        user = AppSettings.getInstance().getUser();

        // Request permissions for notifications on device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // Run thread for notification checking
        if (user != null) {
            startNotificationPolling(user.getUserId());
            startMessagePolling(user.getUserId());
            //startDeviceNotificationPolling(user.getUserId());
        }

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
                hue = 40f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), profileFragment, "PROFILE_FRAGMENT", R.id.mainScreenFrame);
            } else if (selected == R.id.nav_create) {
                selectPostType();
            } else if (selected == R.id.nav_msg) {
                hue = 200f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), new MessageFragment(), "MESSAGE_FRAGMENT", R.id.mainScreenFrame);
            } else if (selected == R.id.nav_search) {
                hue = 330f;
                UISettings.applyFlippedBrightnessGradientBackground(toolbarView, hue, isDarkMode);
                Navigation.replaceFragment(getSupportFragmentManager(), searchFragment, "SEARCH_FRAGMENT", R.id.mainScreenFrame);
            } else {
                hue = 0f;
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

    public void viewPostsByTags(String tagId) {
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
            darkModeToggle.setVisible(currentFragment instanceof HomeFragment);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // Handle menu item clicks, including the dark mode toggle.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_toggle_dark_mode) {
            boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(this);
            DarkModePreferences.setDarkModeEnabled(this, !isDarkMode);
            recreate();
            return true;

        } else if (id == R.id.action_settings) {
            Navigation.replaceFragment(getSupportFragmentManager(), new NotificationSettingsFragment(), "NOTIFICATION_SETTINGS_FRAGMENT", R.id.mainScreenFrame);
            return true;

        } else if (id == R.id.notificationButton) {
            Navigation.replaceFragment(getSupportFragmentManager(), new NotificationFragment(), "NOTIFICATION_FRAGMENT", R.id.mainScreenFrame);
            return true;

        } else if (id == R.id.log_out) {
            AppSettings.setUsernameValue(this, "");
            AppSettings.setPasswordValue(this, "");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
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

    private void selectPostType() {
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
                label.setTextSize(10);
            }
        }
        MenuItem checkedItem = bottomNav.getMenu().findItem(checkedItemId);
        if (checkedItem != null && checkedItem.getActionView() != null) {
            TextView label = checkedItem.getActionView().findViewById(R.id.bottom_nav_label);
            int selectedColor = Color.HSVToColor(new float[]{hue, 1f, 1f});
            int shadowColor = darkenColor(selectedColor);
            label.setShadowLayer(8, 6, 6, shadowColor);
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


    // Start to check if the user has any notifications (follows, likes, comments, tags)
    public void startNotificationPolling(int userId) {
        if (isPollingNotifications) return;
        isPollingNotifications = true;

        notificationPollingRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    try {
                        boolean hasNewNotification = notificationClient.getNotificationFlag(userId);
                        runOnUiThread(() -> {
                            showNotificationDotOnProfile(hasNewNotification);

                            if (hasNewNotification) {
                                showNotificationDotOnProfile(true);

                                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainScreenFrame);
                                currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainScreenFrame);
                                if (currentFragment instanceof ProfileFragment) {
                                    ((ProfileFragment) currentFragment).setNotificationDotVisible(true);
                                }

                                //showDeviceNotification("SoundPalette", "Your work sparked a response!");

                                stopNotificationPolling();
                            } else {
                                notificationPollingHandler.postDelayed(this, 5000);
                            }
                        });
                    } catch (IOException e) {
                        notificationPollingHandler.postDelayed(this, 5000);
                    }
                }).start();
            }
        };

        notificationPollingHandler.post(notificationPollingRunnable);
    }

    public void stopNotificationPolling() {
        isPollingNotifications = false;
        notificationPollingHandler.removeCallbacks(notificationPollingRunnable);
    }


    // Start to check if the user has any message notifications
    public void startMessagePolling(int userId) {
        if (isPollingMessages) return;
        isPollingMessages = true;

        messagePollingRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    try {
                        boolean hasNewMessages = notificationClient.getMessageFlag(userId);
                        runOnUiThread(() -> {
                            showMessageDotOnMessages(hasNewMessages);

                            if (!hasNewMessages) {
                                messagePollingHandler.postDelayed(this, 5000);
                            } else {
                                stopMessagePolling(); // You can decide whether to stop or keep polling
                            }
                        });
                    } catch (IOException e) {
                        messagePollingHandler.postDelayed(this, 5000);
                    }
                }).start();
            }
        };

        messagePollingHandler.post(messagePollingRunnable);
    }

    public void stopMessagePolling() {
        isPollingMessages = false;
        messagePollingHandler.removeCallbacks(messagePollingRunnable);
    }


    public void showNotificationDotOnProfile(boolean show) {
        BottomNavigationView bottomNav = binding.bottomNavigationView;
        View profileItemView = bottomNav.findViewById(R.id.nav_profile);

        if (profileItemView != null) {
            profileItemView.post(() -> {
                View dot = profileItemView.findViewById(R.id.notification_dot_overlay);
                if (dot == null && show) {
                    dot = new View(this);
                    dot.setId(R.id.notification_dot_overlay);
                    dot.setBackgroundResource(R.drawable.round_red_shape);
                    int size = (int) getResources().getDimension(R.dimen.dot_size);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
                    params.topMargin = 4;
                    params.rightMargin = 44;
                    params.gravity = Gravity.END | Gravity.TOP;
                    ((ViewGroup) profileItemView).addView(dot, params);
                } else if (dot != null) {
                    dot.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }


    public void showMessageDotOnMessages(boolean show) {
        BottomNavigationView bottomNav = binding.bottomNavigationView;
        View messageItemView = bottomNav.findViewById(R.id.nav_msg);

        if (messageItemView != null) {
            messageItemView.post(() -> {
                View dot = messageItemView.findViewById(R.id.message_dot_overlay);
                if (dot == null && show) {
                    dot = new View(this);
                    dot.setId(R.id.message_dot_overlay);
                    dot.setBackgroundResource(R.drawable.round_red_shape);
                    int size = (int) getResources().getDimension(R.dimen.dot_size);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
                    params.topMargin = 4;
                    params.rightMargin = 44;
                    params.gravity = Gravity.END | Gravity.TOP;
                    ((ViewGroup) messageItemView).addView(dot, params);
                } else if (dot != null) {
                    dot.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
/*
    public void startDeviceNotificationPolling(int userId) {
        if (isPollingDeviceNotifications) return;
        isPollingDeviceNotifications = true;

        deviceNotificationPollingRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    try {
                        boolean shouldNotify = notificationClient.getDeviceNotificationFlag(userId);
                        runOnUiThread(() -> {
                            if (shouldNotify) {
                                showDeviceNotification("SoundPalette", "Your work sparked a response!");
                                stopDeviceNotificationPolling();
                            } else {
                                deviceNotificationPollingHandler.postDelayed(this, 5000);
                            }
                        });
                    } catch (IOException e) {
                        deviceNotificationPollingHandler.postDelayed(this, 5000);
                    }
                }).start();
            }
        };

        deviceNotificationPollingHandler.post(deviceNotificationPollingRunnable);
    }

    public void stopDeviceNotificationPolling() {
        isPollingDeviceNotifications = false;
        deviceNotificationPollingHandler.removeCallbacks(deviceNotificationPollingRunnable);
    }


    private void showDeviceNotification(String title, String message) {
        String channelId = "default_channel";
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "SoundPalette Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for SoundPalette app notifications");
            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sp_logo_notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.baseline_music_note_48)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
*/
}