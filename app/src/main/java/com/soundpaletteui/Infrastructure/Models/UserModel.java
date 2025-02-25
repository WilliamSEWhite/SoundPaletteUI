package com.soundpaletteui.Infrastructure.Models;

public class UserModel {

    int UserId;
    private String Username;
    private String Password;

    private UserInfoModel UserInfo;

    public int getUserId() {
        return UserId;
    }
    public String getUsername() {
        return Username;
    }
    public String getPassword() {
        return Password;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
    public UserInfoModel getUserInfo() {
        return UserInfo;
    }
    public void setUserInfo(UserInfoModel userInfo) {
        this.UserInfo = userInfo;
    }


    public UserModel(int userId, String username, String password){
        this.UserId = userId;
        this.Username = username;
        this.Password = password;

    }

    public UserModel(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

}
