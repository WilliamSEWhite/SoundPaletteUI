package com.soundpaletteui.Infrastructure.Models.Notifications;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationSettingModel {
    @SerializedName("NotificationSettingId")
    private int notificationSettingId;
    @SerializedName("Value")
    private boolean value;
    @SerializedName("SettingName")
    private String settingName;
    @SerializedName("NotificationTypeId")
    private int notificationTypeId;
    @SerializedName("UserId")
    private int userId;

    public NotificationSettingModel(int notificationSettingId, boolean value, String settingName, int notificationTypeId, int userId)
    {
        this.notificationSettingId = notificationSettingId;
        this.value = value;
        this.settingName = settingName;
        this.notificationTypeId = notificationTypeId;
        this.userId = userId;

    }
    public boolean getValue() { return value; }
    public String getNotificationSettingName() { return settingName; }
    public void setValue(boolean value) { this.value = value; }
}
