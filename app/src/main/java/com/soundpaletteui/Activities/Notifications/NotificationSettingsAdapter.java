package com.soundpaletteui.Activities.Notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;
import com.soundpaletteui.R;

import java.util.List;

public class NotificationSettingsAdapter extends RecyclerView.Adapter<com.soundpaletteui.Activities.Notifications.NotificationSettingsAdapter.NotificationSettingsViewHolder> {

    private List<NotificationSettingModel> notificationSettingList;
    private Context context;

    public NotificationSettingsAdapter(List<NotificationSettingModel> notificationSettingList) {
        this.notificationSettingList = notificationSettingList;
    }

    @NonNull
    @Override
    public NotificationSettingsAdapter.NotificationSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_notification_setting, parent, false);
        return new NotificationSettingsAdapter.NotificationSettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationSettingsAdapter.NotificationSettingsViewHolder holder, int position) {

        NotificationSettingModel notificationSetting = notificationSettingList.get(position);
        holder.notificationSettingName.setText(notificationSetting.getNotificationSettingName());

        boolean currentSetting = notificationSetting.getValue();
        holder.toggleButton.setChecked(currentSetting);

        boolean newSetiing = holder.toggleButton.isChecked();
    }

    @Override
    public int getItemCount() {
        return notificationSettingList != null ? notificationSettingList.size() : 0;
    }


    public static class NotificationSettingsViewHolder extends RecyclerView.ViewHolder {
        TextView notificationSettingName;
        ToggleButton toggleButton;

        public NotificationSettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationSettingName = itemView.findViewById(R.id.notificationSettingName);
            toggleButton = itemView.findViewById(R.id.toggleButton);;
        }
    }
}
