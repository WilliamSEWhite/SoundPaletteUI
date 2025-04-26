package com.soundpaletteui.Infrastructure.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.soundpaletteui.Infrastructure.Models.User.UserModel;

// Stores and manages the logged-in user's information and credentials
public class AppSettings {
    private static final AppSettings appSettingsInstance = new AppSettings();
    private static final String USERNAME = "username";
    private static final String USERNAME_VALUE = "username_value";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_VALUE = "password_value";
    private UserModel user = new UserModel();

    // Default constructor
    public AppSettings() { }

    // Gets the user's Id
    public int getUserId() {
        return user.getUserId();
    }

    // Gets the user's username
    public String getUsername() {
        return user.getUsername();
    }

    // Gets the current UserModel
    public UserModel getUser() {
        return user;
    }

    // Sets the current UserModel
    public void setUser(UserModel user) {
        this.user = user;
    }

    // Returns the shared instance of AppSettings
    public static AppSettings getInstance() {
        return appSettingsInstance;
    }

    // Gets the stored username value from SharedPreferences
    public static String getUsernameValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        return prefs.getString(USERNAME_VALUE, "");
    }

    // Saves the username value to SharedPreferences
    public static void setUsernameValue(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        prefs.edit().putString(USERNAME_VALUE, username).apply();
    }

    // Gets the stored password value from SharedPreferences
    public static String getPasswordValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PASSWORD, Context.MODE_PRIVATE);
        return prefs.getString(PASSWORD_VALUE, "");
    }

    // Saves the password value to SharedPreferences
    public static void setPasswordValue(Context context, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PASSWORD, Context.MODE_PRIVATE);
        prefs.edit().putString(PASSWORD_VALUE, password).apply();
    }
}
