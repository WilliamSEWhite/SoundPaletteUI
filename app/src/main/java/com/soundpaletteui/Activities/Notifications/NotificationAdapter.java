package com.soundpaletteui.Activities.Notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;
import com.soundpaletteui.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<com.soundpaletteui.Activities.Notifications.NotificationAdapter.NotificationViewHolder> {
    private List<NotificationModel> notificationList;
    private Context context;

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_notification_setting, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {

        NotificationModel notificationModel = notificationList.get(position);
        holder.notificationMessage.setText(notificationModel.getMessage());

        int notificationIdType = notificationModel.getNotificationIdType();
        int referenceId = notificationModel.getReferenceId();

        holder.userProfilePicture.setOnClickListener(v -> {

            if (notificationIdType==1) {
                Log.d("NOTIFICATION ADAPTER", "go to chatroom #"+referenceId);
            } else {
                Log.d("NOTIFICATION ADAPTER", "go to profile"+referenceId);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView usernameDisplay, notificationMessage;
        ImageButton userProfilePicture;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameDisplay = itemView.findViewById(R.id.usernameDisplay);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);;
        }
    }
}