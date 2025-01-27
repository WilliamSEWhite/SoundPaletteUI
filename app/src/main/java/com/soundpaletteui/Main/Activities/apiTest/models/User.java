package com.soundpaletteui.Main.Activities.apiTest.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("Username")
    private String username;
    @SerializedName("Password")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public void setPassword(String p) {
        this.password = p;
    }

}
