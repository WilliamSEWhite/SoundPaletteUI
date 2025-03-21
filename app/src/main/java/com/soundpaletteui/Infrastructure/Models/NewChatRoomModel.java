package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewChatRoomModel {
    @SerializedName("Name")
    public String name;
    @SerializedName("Users")
    public List<String> users;

    public NewChatRoomModel(String name, List<String> users)
    {
        this.name = name;
        this.users = users;
    }
}
