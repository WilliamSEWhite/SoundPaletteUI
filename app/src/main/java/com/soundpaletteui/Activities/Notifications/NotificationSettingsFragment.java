package com.soundpaletteui.Activities.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Messages.ChatroomAdapter;
import com.soundpaletteui.R;

public class NotificationSettingsFragment extends Fragment {

    private TextView notificationTitle;

    private RecyclerView recyclerViewNotifications;
    //private <NotificationSettings> allNotificationSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationTitle = rootView.findViewById(R.id.notificationTitle);
        notificationTitle.setText("Notification Settings");

        recyclerViewNotifications = rootView.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new GridLayoutManager(getContext(), 1));

        return rootView;
    }

    private void setupRecyclerView() {
        //recyclerViewNotifications.setAdapter(new NotificationSettingsAdapter(allNotificationSettings));
    }

}
