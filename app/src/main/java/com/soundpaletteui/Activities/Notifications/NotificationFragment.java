package com.soundpaletteui.Activities.Notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.NotificationClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Displays the user's notification screen, loading and showing a list of notifications using RecyclerView.
public class NotificationFragment  extends Fragment {
    private RecyclerView recyclerViewNotifications;
    private List<NotificationModel> allNotifications = new ArrayList<>();
    private final NotificationClient notificationClient = SPWebApiRepository.getInstance().getNotificationClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerViewNotifications = rootView.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new GridLayoutManager(getContext(), 1));
        initComponents(rootView);

        return rootView;
    }

    // Initializes UI elements, background, and starts loading notifications
    private void initComponents(View view) {
        // Get and configure the emoji background
        com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView emojiBackground = view.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView.PATTERN_GRID);
        emojiBackground.setAlpha(0.2f);

        // Apply gradient background based on dark mode
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(view.getContext());
        UISettings.applyBrightnessGradientBackground(view, 165f, isDarkMode);

        // Set up RecyclerView again if the view was refreshed
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new GridLayoutManager(getContext(), 1));

        new GetNotificationsTask().execute();
    }


    // Get the notification list
    private class GetNotificationsTask extends AsyncTask<Void, Void, List<NotificationModel>> {
        @Override
        protected List<NotificationModel> doInBackground(Void... voids) {
            try {
                return notificationClient.getNotifications();
            } catch (IOException e) {
                Log.e("NOTIFICATION", "Error fetching Notifications", e);
                return new ArrayList<>();
            }
        }
        @Override
        protected void onPostExecute(List<NotificationModel> notificationList) {
            if (notificationList == null) {
                notificationList = new ArrayList<>();
            }

            // Update local list and refresh the RecyclerView
            allNotifications.clear();
            allNotifications.addAll(notificationList);
            setupRecyclerView();
        }
    }

    // Sets up the RecyclerView adapter with the loaded notifications
    private void setupRecyclerView() {
        recyclerViewNotifications.setAdapter(new NotificationAdapter(allNotifications));
    }

}
