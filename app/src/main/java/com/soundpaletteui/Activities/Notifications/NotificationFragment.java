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


    private void initComponents(View view) {
        // Get and configure the emoji background
        com.soundpaletteui.Views.EmojiBackgroundView emojiBackground = view.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(com.soundpaletteui.Views.EmojiBackgroundView.PATTERN_GRID);
        emojiBackground.setAlpha(0.2f);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(view.getContext());
        UISettings.applyBrightnessGradientBackground(view, 240f, isDarkMode);

        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new GridLayoutManager(getContext(), 1));

        getAllNotifications();
        //new GetNotificationsTask().execute();
    }


    // Get the notification list
    private class GetNotificationsTask extends AsyncTask<Void, Void, List<NotificationModel>> {
        @Override
        protected List<NotificationModel> doInBackground(Void... voids) {
            try {
                return notificationClient.getNotification();
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
            allNotifications.clear();
            allNotifications.addAll(notificationList);
            setupRecyclerView();
        }
    }

    private void getAllNotifications() {
        allNotifications.clear();

        Date now = new Date();
        allNotifications.add(new NotificationModel(1, 1, "Duet with Destiny", "has a new message!", now));
        allNotifications.add(new NotificationModel(2, 3, "user3", "followed you!", now));
        allNotifications.add(new NotificationModel(3, 3, "user3", "liked your post!", now));
        allNotifications.add(new NotificationModel(4, 3, "user3", "tagged in a post!", now));
        allNotifications.add(new NotificationModel(5, 3, "user3", "comment on your post!", now));

        setupRecyclerView();
    }


    private void setupRecyclerView() {
        recyclerViewNotifications.setAdapter(new NotificationAdapter(allNotifications));
    }

}
