package com.soundpaletteui.Infrastructure.Models.Notifications;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationSettingModel {
    @SerializedName("notificationId")
    private int notificationId;
    @SerializedName("value")
    private Boolean value;
    @SerializedName("settingName")
    private String settingName;


    public NotificationSettingModel(int notificationId, boolean value, String settingName)
    {
        this.notificationId = notificationId;
        this.value = value;
        this.settingName = settingName;
    }
    public boolean getValue() { return value; }
    public String getNotificationSettingName() { return settingName; }
}
