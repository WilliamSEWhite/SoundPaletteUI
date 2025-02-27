package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserInfoModel {
    @SerializedName("userInfoId")
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
    //public Date DOB;
    @SerializedName("dob")
    public Date Dob;
    @SerializedName("dateCreated")
    public String dateCreated;
    //public Date DateCreated;

    /* may need this later **
    public UserInfoModel(int userInfoId, int userId, int locationId, String email, String phone, Date dOB, Date dateCreated)
    {
        UserInfoId = userInfoId;
        UserId = userId;
        LocationId = locationId;
        Email = email;
        Phone = phone;
        DOB = dOB;
        DateCreated = dateCreated;
    }*/

    public UserInfoModel(int userId, int locationId, String email, String phone, Date dob, String dateCreated) {
        UserId = userId;
        LocationId = locationId;
        Email = email;
        Phone = phone;
        this.Dob = dob;
        this.dateCreated = dateCreated;
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
