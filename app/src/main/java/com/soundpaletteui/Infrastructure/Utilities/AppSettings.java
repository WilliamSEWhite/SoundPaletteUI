package com.soundpaletteui.Infrastructure.Utilities;

import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

public class AppSettings {
    private static final AppSettings appSettingsInstance = new AppSettings();

    private UserModel user;

    public int getUserId(){ return user.getUserId(); }
    public UserModel getUser(){ return user; }

    public void setUser(UserModel user){ this.user = user; }

    public static AppSettings getInstance(){ return appSettingsInstance; }

    public AppSettings(){

    }
}
