package com.soundpaletteui.Infrastructure.Models.Notifications;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationModel {
    @SerializedName("notificationIdType")
    private int notificationIdType;
    @SerializedName("referenceId")
    private int referenceId;
    @SerializedName("referenceName")
    private String referenceName;
    @SerializedName("Message")
    private String Message;
    @SerializedName("createdDate")
    private Date createdDate;

    public NotificationModel(int notificationIdType, int referenceId, String referenceName, String Message, Date createdDate) {
        this.notificationIdType = notificationIdType;
        this.referenceId = referenceId;
        this.referenceName = referenceName;
        this.Message = Message;
        this.createdDate = createdDate;
    }


    public int getNotificationIdType() { return notificationIdType; }
    public int getReferenceId() { return referenceId; }
    public String getReferenceName() { return referenceName; }
    public String getMessage() { return Message; }
    public Date getcreatedDate() { return createdDate; }

}
