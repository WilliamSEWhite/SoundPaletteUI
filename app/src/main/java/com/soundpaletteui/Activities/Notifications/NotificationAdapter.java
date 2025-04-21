package com.soundpaletteui.Activities.Notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.Messages.ChatroomFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
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

        int notificationIdType = notificationModel.getNotificationTypeId();
        int referenceId = notificationModel.getReferenceId();
        String referenceName = notificationModel.getReferenceName();
        holder.usernameDisplay.setText(referenceName);

        holder.notificationAdapter.setOnClickListener(v -> {
            if (notificationIdType == 1) {
                Log.d("NOTIFICATION ADAPTER", "go to chatroom #" + referenceName);
                ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(referenceId, referenceName);
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                Navigation.replaceFragment(fragmentManager, chatroomFragment, "CHATROOM_FRAGMENT", R.id.mainScreenFrame);

            } else {
                Log.d("NOTIFICATION ADAPTER", "go to profile " + referenceName);
                ProfileViewFragment profileViewFragment = ProfileViewFragment.newInstance(referenceName);
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                Navigation.replaceFragment(fragmentManager, profileViewFragment, "PROFILE_VIEW_FRAGMENT", R.id.mainScreenFrame);

            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView usernameDisplay, notificationMessage;
        LinearLayout notificationAdapter;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameDisplay = itemView.findViewById(R.id.usernameDisplay);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationAdapter = itemView.findViewById(R.id.notificationAdapter);
        }
    }
}
