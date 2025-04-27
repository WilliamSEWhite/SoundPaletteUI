package com.soundpaletteui.Infrastructure.Models.Chat;

import com.google.gson.annotations.SerializedName;

public class ChatroomModelLite {
    @SerializedName("ChatroomId")
    private int chatroomId;
    @SerializedName("Name")
    private String name;

    public ChatroomModelLite() { }
    public ChatroomModelLite(int chatroomId, String name) {
        this.chatroomId = chatroomId;
        this.name = name;
    }

    public int getChatroomId(){ return chatroomId; }
    public String getName(){ return name; }

}
