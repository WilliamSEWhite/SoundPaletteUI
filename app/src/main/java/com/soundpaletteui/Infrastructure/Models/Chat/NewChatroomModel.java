package com.soundpaletteui.Infrastructure.Models.Chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewChatroomModel {
    @SerializedName("Name")
    public String name;
    @SerializedName("Users")
    public List<String> users;

    public NewChatroomModel(String name, List<String> users)
    {
        this.name = name;
        this.users = users;
    }
}
