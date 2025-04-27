package com.soundpaletteui.Infrastructure.Models.User;

import com.google.gson.annotations.SerializedName;

public class UserProfileModel {
    @SerializedName("UserProfileId")
    private int userProfileId;
    @SerializedName("UserId")
    private int userId;
    @SerializedName("Bio")
    private String bio;
    @SerializedName("Picture")
    private String pic;
    @SerializedName("FollowerCount")
    private int followerCount;
    @SerializedName("FollowingCount")
    private int followingCount;

    public UserProfileModel(int userId, String bio, String pic) {
        this.userId = userId;
        this.bio = bio;
        this.pic = pic;
        System.out.println("bio from model: " + bio);
    }

    // Getter methods
    public int getUserProfileId() {
        return userProfileId;
    }
    public int getUserId() {
        return userId;
    }
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

    // Setter methods
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }

}
