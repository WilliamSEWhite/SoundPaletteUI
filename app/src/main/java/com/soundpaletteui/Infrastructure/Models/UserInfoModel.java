package com.soundpaletteui.Infrastructure.Models;

import java.util.Date;

public class UserInfoModel {
    public int UserInfoId;
    public int UserId;
    public UserModel User;
    public int LocationId;
    public LocationModel Location;
    public String Email;
    public String Phone;
    public Date DOB;
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

}
