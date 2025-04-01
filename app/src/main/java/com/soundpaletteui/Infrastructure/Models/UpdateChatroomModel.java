package com.soundpaletteui.Infrastructure.Models;

import java.util.List;

public class UpdateChatroomModel {
    private int chatroomId;
    private String newChatroomName;
    private List<String> memberUsernames;

    public UpdateChatroomModel(int chatroomId, String newChatroomName, List<String> memberUsernames) {
        this.chatroomId = chatroomId;
        this.newChatroomName = newChatroomName;
        this.memberUsernames = memberUsernames;
    }

    public int getChatroomId() {
        return chatroomId;
    }

    public String getNewChatroomName() {
        return newChatroomName;
    }

    public List<String> getMemberUsernames() {
        return memberUsernames;
    }

    public void setNewChatroomName(String newChatroomName) {
        this.newChatroomName = newChatroomName;
    }

    public void setMemberUsernames(List<String> memberUsernames) {
        this.memberUsernames = memberUsernames;
    }
}
