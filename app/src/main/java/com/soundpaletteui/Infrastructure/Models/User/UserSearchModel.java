package com.soundpaletteui.Infrastructure.Models.User;

import com.google.gson.annotations.SerializedName;

public class UserSearchModel {
    @SerializedName("Username")
    private String username;
    @SerializedName("FollowerCount")
    private int followerCount;
    @SerializedName("IsFollowing")
    private boolean isFollowing;

    public UserSearchModel(String username, int followerCount, boolean isFollowing){
        this.username = username;
        this.followerCount = followerCount;
        this.isFollowing = isFollowing;
    }

    public String getUsername() {
        return username;
    }
    public int getFollowerCount() {
        return followerCount;
    }

    public void setIsFollowing(boolean isFollowing){
        this.isFollowing = isFollowing;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
}
