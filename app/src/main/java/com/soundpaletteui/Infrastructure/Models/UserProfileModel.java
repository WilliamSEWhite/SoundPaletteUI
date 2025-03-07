package com.soundpaletteui.Infrastructure.Models;

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

    public UserProfileModel(int userId, String bio, String pic) {
        this.userId = userId;
        this.bio = bio;
        this.pic = pic;
        System.out.println("bio from model: " + bio);
    }

    /** setter methods */
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }

    /** getter methods */
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
}
