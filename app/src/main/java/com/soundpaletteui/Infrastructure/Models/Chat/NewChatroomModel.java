package com.soundpaletteui.Infrastructure.Models.Chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewChatroomModel {
    @SerializedName("Name")
    public String name;
    @SerializedName("Users")
    public List<String> users;
    @SerializedName("CreatedById")
    public int createdById;

    public NewChatroomModel(String name, List<String> users, int createdById)
    {
        this.name = name;
        this.users = users;
        this.createdById = createdById;
    }
}
