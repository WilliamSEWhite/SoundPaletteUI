package com.soundpaletteui.Infrastructure.Models.User;

import com.google.gson.annotations.SerializedName;
import com.soundpaletteui.Infrastructure.Models.LocationModel;

import java.util.Date;

public class UserInfoModel {
    @SerializedName("UserInfoId")
    public int UserInfoId;
    @SerializedName("UserId")
    public int UserId;
    public UserModel User;
    @SerializedName("LocationId")
    public int LocationId;
    public LocationModel Location;
    @SerializedName("Email")
    public String Email;
    @SerializedName("Phone")
    public String Phone;
    @SerializedName("DOB")
    public Date Dob;
    @SerializedName("DateCreated")
    public String dateCreated;

    public UserInfoModel(int userId, int locationId, String email, String phone, Date dob, String dateCreated) {
        UserId = userId;
        LocationId = locationId;
        Email = email;
        Phone = phone;
        this.Dob = dob;
        this.dateCreated = dateCreated;
        System.out.println("Location in Model 1: " + LocationId);
    }

    // Constructor for update user profile
    public UserInfoModel(int userId, int locationId, String email, String phone, Date dob) {
        UserId = userId;
        Email = email;
        Phone = phone;
        Dob = dob;
        LocationId = locationId;
        System.out.println("Location in Model: " + LocationId);
    }

    public int getLocationId() {
        return LocationId;
    }
    public String getEmail() {
        return Email;
    }
    public String getPhone() {
        return Phone;
    }
    public Date getDob() {
        return Dob;
    }

}
