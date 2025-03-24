package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChatroomModel {
    @SerializedName("ChatRoomId")

    private int chatRoomId;
    @SerializedName("ChatRoomName")
    private String chatRoomName;
    @SerializedName("LastMessage")
    private String lastMessage;
    @SerializedName("LastMessageDate")
    private Date lastMessageDate;
    @SerializedName("LastMessageBy")
    private String lastMessageBy;
    @SerializedName("IsGroupChat")
    private Boolean isGroupChat;

    public ChatroomModel(int chatRoomId, String chatRoomName, String lastMessage, Date lastMessageDate, String lastMessageBy, boolean isGroupChat)
    {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.lastMessageBy = lastMessageBy;
        this.isGroupChat = isGroupChat;
    }
    public int getChatRoomId() { return chatRoomId; }
    public String getChatRoomName() { return chatRoomName; }
    public String getLastMessage() { return isGroupChat ? lastMessageBy + ": " + lastMessage : lastMessage; }
    public Date getLastMessageDate() { return lastMessageDate; }

}
