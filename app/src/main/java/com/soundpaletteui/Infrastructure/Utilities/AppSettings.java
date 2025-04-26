package com.soundpaletteui.Infrastructure.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.soundpaletteui.Infrastructure.Models.User.UserModel;

public class AppSettings {
    private static final AppSettings appSettingsInstance = new AppSettings();
    private static final String USERNAME = "username";
    private static final String USERNAME_VALUE = "username_value";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_VALUE = "password_value";
    private UserModel user = new UserModel();

    /** default constructor */
    public AppSettings() { }

    public int getUserId() {

        if(user != null)
            return user.getUserId();
        else
            return 0;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public static AppSettings getInstance() {
        return appSettingsInstance;
    }

    public static String getUsernameValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        return prefs.getString(USERNAME_VALUE, "");
    }

    public static void setUsernameValue(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        prefs.edit().putString(USERNAME_VALUE, username).apply(); // Save the preference
    }

    public static String getPasswordValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PASSWORD, Context.MODE_PRIVATE);
        return prefs.getString(PASSWORD_VALUE, "");
    }

    public static void setPasswordValue(Context context, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PASSWORD, Context.MODE_PRIVATE);
        prefs.edit().putString(PASSWORD_VALUE, password).apply(); // Save the preference
    }
}
