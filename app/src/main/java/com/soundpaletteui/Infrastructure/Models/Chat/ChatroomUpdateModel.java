package com.soundpaletteui.Infrastructure.Models.Chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatroomUpdateModel {
    @SerializedName("ChatroomId")

    private int chatroomId;
    @SerializedName("ChatroomName")

    private String chatroomName;
    @SerializedName("MembersToAdd")
    private List<String> membersToAdd;
    @SerializedName("MembersToRemove")
    private List<String> membersToRemove;


    public ChatroomUpdateModel(int chatroomId, String newChatroomName, List<String> membersToAdd, List<String> membersToRemove) {
        this.chatroomId = chatroomId;
        this.chatroomName = newChatroomName;
        this.membersToAdd = membersToAdd;
        this.membersToRemove = membersToRemove;

    }
}
