package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserInfoModel {
    public int UserInfoId;
    @SerializedName("userId")
    public int UserId;
    public UserModel User;
    @SerializedName("locationId")
    public int LocationId;
    public LocationModel Location;
    @SerializedName("email")
    public String Email;
    @SerializedName("phone")
    public String Phone;
    public Date DOB;
    @SerializedName("dob")
    public String Dob;
    @SerializedName("dateCreated")
    public String dateCreated;
    public Date DateCreated;

    public UserInfoModel(int userInfoId, int userId, int locationId, String email, String phone, Date dOB, Date dateCreated)
    {
        UserInfoId = userInfoId;
        UserId = userId;
        LocationId = locationId;
        Email = email;
        Phone = phone;
        DOB = dOB;
        DateCreated = dateCreated;
    }

    public UserInfoModel(int userId, int locationId, String email, String phone, String dob, String dateCreated) {
        System.out.println("I am here");
        UserId = userId;
        LocationId = locationId;
        Email = email;
        Phone = phone;
        this.Dob = dob;
        this.dateCreated = dateCreated;

        System.out.println("dob: " + this.Dob);
        System.out.println("dateCreated: " + this.dateCreated);
    }

}
