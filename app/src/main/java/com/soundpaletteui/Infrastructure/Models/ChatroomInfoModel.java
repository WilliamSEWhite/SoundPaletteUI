package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatroomInfoModel {
    @SerializedName("ChatroomId")
    private int chatroomId;
    @SerializedName("ChatroomName")
    private String chatroomName;
    @SerializedName("ChatroomMembers")
    private List<String> chatroomMembers;

    public ChatroomInfoModel(int chatroomId, String chatroomName, List<String> chatroomMembers) {
        this.chatroomId = chatroomId;
        this.chatroomName = chatroomName;
        this.chatroomMembers = chatroomMembers;
    }

    public int getChatroomId(){ return chatroomId; }
    public String getChatroomName() { return chatroomName; }
    public List<String> getChatroomMembers() { return chatroomMembers; }


}
