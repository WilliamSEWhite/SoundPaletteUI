package com.soundpaletteui.Infrastructure.Models.Chat;

import com.google.gson.annotations.SerializedName;

public class NewMessageModel {
    @SerializedName("UserId")
    private int userId;
    @SerializedName("Message")
    private String message;
    @SerializedName("ChatRoomId")
    private int chatRoomId;
    public NewMessageModel(int userId, String message, int chatRoomId) {
        this.userId = userId;
        this.message = message;
        this.chatRoomId = chatRoomId;
    }
}
