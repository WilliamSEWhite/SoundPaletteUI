package com.soundpaletteui.Activities.Messages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModel;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.util.List;
public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ChatroomViewHolder> {

    private List<ChatroomModel> chatroomList;
    private Context context;

    public ChatroomAdapter(List<ChatroomModel> chatroomList) {
        this.chatroomList = chatroomList;
    }

    @NonNull
    @Override
    public ChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_chatroom, parent, false);
        return new ChatroomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatroomViewHolder holder, int position) {
        ChatroomModel chatroom = chatroomList.get(position);
        holder.chatroomName.setText(chatroom.getChatRoomName());
        holder.lastMessage.setText(chatroom.getLastMessage());
        holder.lastMessageDate.setText(chatroom.getLastMessageDate().toString());

        // Adapter onClick Listener
        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            int chatroomId = chatroom.getChatRoomId();

            // Open the chatroom for the selected ChatroomAdapter
            ChatroomFragment chatroomFragment = ChatroomFragment.newInstance(chatroomId, chatroom.getChatRoomName());

            // Replace the fragment with the chatroomFragment
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, chatroomFragment, "CHATROOM_FRAGMENT", R.id.mainScreenFrame);
            /*FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.mainScreenFrame, chatroomFragment);
            transaction.addToBackStack(null);
            transaction.commit();*/
        });
    }

    @Override
    public int getItemCount() {
        return chatroomList != null ? chatroomList.size() : 0;
    }

    public static class ChatroomViewHolder extends RecyclerView.ViewHolder {
        TextView chatroomName, lastMessage, lastMessageDate;

        public ChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            chatroomName = itemView.findViewById(R.id.chatroomName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastMessageDate = itemView.findViewById(R.id.lastMessageDate);
        }
    }
}