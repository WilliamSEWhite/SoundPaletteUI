package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

public class UserProfileModelLite {

    @SerializedName("UserProfileId")
    private int userProfileId;
    @SerializedName("Username")
    private String username;
    @SerializedName("Bio")
    private String bio;
    @SerializedName("Picture")
    private String pic;
    @SerializedName("FollowerCount")
    private int followerCount;
    @SerializedName("FollowingCount")
    private int followingCount;
    @SerializedName("isFollowing")
    private boolean isFollowing;

    public UserProfileModelLite(String username) {
        this.username = username;
    }

    public UserProfileModelLite(String username, boolean isFollowing) {
        this.username = username;
    }

    public UserProfileModelLite(String username, String bio, String pic, int followerCount, int followingCount, boolean isFollowing) {
        this.username = username;
        this.bio = bio;
        this.pic = pic;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.isFollowing = isFollowing;
        System.out.println("bio from model: " + bio);
    }

    /** setter methods */
    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    /** getter methods */
    public String getBio() {
        return bio;
    }
    public String getPic() {
        return pic;
    }
    public int getFollowerCount() {
        return followerCount;
    }
    public int getFollowingCount() {
        return followingCount;
    }
    public boolean isFollowing() {
        return isFollowing;
    }
}
