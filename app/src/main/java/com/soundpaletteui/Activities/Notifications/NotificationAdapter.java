package com.soundpaletteui.Activities.Notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Messages.ChatroomFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationModel> notificationList;
    private Context context;

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_notification, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        NotificationModel notificationModel = notificationList.get(position);
        holder.notificationMessage.setText(notificationModel.getMessage());

        int notificationIdType = notificationModel.getNotificationIdType();
        int referenceId = notificationModel.getReferenceId();
        String referenceName = notificationModel.getReferenceName();
        holder.usernameDisplay.setText(referenceName);

        holder.userProfilePicture.setOnClickListener(v -> {
            if (notificationIdType == 1) {
                Log.d("NOTIFICATION ADAPTER", "go to chatroom #" + referenceName);
                ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(referenceId, referenceName);
                // Navigation logic here
            } else {
                Log.d("NOTIFICATION ADAPTER", "go to profile " + referenceName);
                ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(referenceName);
                // Navigation logic here
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView usernameDisplay, notificationMessage;
        ImageView userProfilePicture;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameDisplay = itemView.findViewById(R.id.usernameDisplay);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            userProfilePicture = itemView.findViewById(R.id.userProfilePicture);
        }
    }
}
