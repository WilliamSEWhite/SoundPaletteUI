package com.soundpaletteui.Activities.Notifications;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Messages.NewChatroomFragment;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.Infrastructure.Utilities.DarkModePreferences;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.Infrastructure.Utilities.UISettings;
import com.soundpaletteui.R;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.NotificationClient;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays the Notification Settings screen where users can enable or disable different types of notifications.
public class NotificationSettingsFragment extends Fragment {
    private Button saveButton;
    private RecyclerView recyclerViewNotifications;
    private List<NotificationSettingModel> allNotificationSettings = new ArrayList<>();
    private final NotificationClient notificationClient = SPWebApiRepository.getInstance().getNotificationClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notification_setting, container, false);

        recyclerViewNotifications = rootView.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new GridLayoutManager(getContext(), 1));
        initComponents(rootView);

        // Save button to commit notification settings changes
        saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            try {
                notificationClient.setNotificationSettings(allNotificationSettings);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Success")
                    .setMessage("Notification settings saved!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();

                        // Navigate back to NotificationFragment *after* user clicks OK
                        NotificationFragment notificationFragment = new NotificationFragment();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Navigation.replaceFragment(requireActivity().getSupportFragmentManager(),
                                transaction,
                                notificationFragment,
                                "NEW_NOTIFICATION_FRAGMENT",
                                R.id.mainScreenFrame);
                    })
                    .show();
        });

        return rootView;
    }

    private void initComponents(View view) {
        // Get and configure the emoji background
        com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView emojiBackground = view.findViewById(R.id.emojiBackground);
        emojiBackground.setPatternType(com.soundpaletteui.Infrastructure.Utilities.EmojiBackgroundView.PATTERN_GRID);
        emojiBackground.setAlpha(0.2f);
        boolean isDarkMode = DarkModePreferences.isDarkModeEnabled(view.getContext());
        UISettings.applyBrightnessGradientBackground(view, 165f, isDarkMode);

        // Set up RecyclerView layout
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Fetch notification settings from the API Server
        new GetNotificationSettingsTask().execute();
    }

    // AsyncTask to fetch notification settings from server (currently unused)
    private class GetNotificationSettingsTask extends AsyncTask<Void, Void, List<NotificationSettingModel>> {
        @Override
        protected List<NotificationSettingModel> doInBackground(Void... voids) {
            try {
                return notificationClient.getNotificationSettings();
            } catch (IOException e) {
                Log.e("NOTIFICATION SETTINGS", "Error fetching Notification Settings", e);
                return new ArrayList<>();
            }
        }
        @Override
        protected void onPostExecute(List<NotificationSettingModel> notificationSettingList) {
            if (notificationSettingList == null) {
                notificationSettingList = new ArrayList<>();
            }
            allNotificationSettings.clear();
            allNotificationSettings.addAll(notificationSettingList);
            setupRecyclerView();
        }
    }

    // Binds the adapter to the RecyclerView to display settings
    private void setupRecyclerView() {
        recyclerViewNotifications.setAdapter(new NotificationSettingsAdapter(allNotificationSettings));
    }

}
