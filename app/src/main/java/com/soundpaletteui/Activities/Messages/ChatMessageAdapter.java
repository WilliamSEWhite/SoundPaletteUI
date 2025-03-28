package com.soundpaletteui.Activities.Messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private final List<ChatMessageModel> messageList;
    private final UserModel user;
    private final UserClient userClient;
    private final String username;

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ACTIVE = 2;
    private static final int VIEW_TYPE_INACTIVE = 3;

    public ChatMessageAdapter(List<ChatMessageModel> messageList) {
        this.messageList = messageList;
        this.user = AppSettings.getInstance().getUser();
        this.userClient = SPWebApiRepository.getInstance().getUserClient();
        this.username = (user != null) ? user.getUsername() : null;
    }

    // If the sender == user, then set VIEW_TYPE_USER
    // Else if the sender is active and assign VIEW_TYPE_ACTIVE
    // Else assign VIEW_TYPE_INACTIVE
    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = messageList.get(position);

        if (message.getSentBy() != null && message.getSentBy().equals(username)) {
            return VIEW_TYPE_USER;
        } else if (message.getIsActiveUser()) {
            return VIEW_TYPE_ACTIVE;
        } else {
            return VIEW_TYPE_INACTIVE;
        }
    }

    // Set the adapter type based on the VIEW_TYPE_USER
    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_USER:
                layoutId = R.layout.adapter_chatmsg_user;
                break;
            case VIEW_TYPE_ACTIVE:
                layoutId = R.layout.adapter_chatmsg_active;
                break;
            case VIEW_TYPE_INACTIVE:
            default:
                layoutId = R.layout.adapter_chatmsg_inactive;
                break;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ChatMessageViewHolder(view);
    }

    // Set the message, username and sent date
    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessageModel message = messageList.get(position);

        holder.textMessage.setText(message.getMessage());
        holder.textMessageUsername.setText(message.getSentBy());

        // Format and display message timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        if (message.getSentDate() != null) {
            holder.textMessageDate.setText(dateFormat.format(message.getSentDate()));
        } else {
            holder.textMessageDate.setText("Unknown date");
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Find the objects in adapter_chatmessage_user.xml (or adapter_chatmessage_other.xml)
    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        ImageButton senderProfile;
        TextView textMessageUsername, textMessageDate, textMessage;

        public ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderProfile = itemView.findViewById(R.id.senderProfile);
            textMessageUsername = itemView.findViewById(R.id.textMessageUsername);
            textMessageDate = itemView.findViewById(R.id.textMessageDate);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
