package com.soundpaletteui.Infrastructure.Models.Notifications;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationModel {
    @SerializedName("NotificationTypeId")
    private int notificationTypeId;
    @SerializedName("ReferenceId")
    private int referenceId;
    @SerializedName("ReferenceName")
    private String referenceName;
    @SerializedName("Message")
    private String message;
    @SerializedName("CreatedDate")
    private Date createdDate;

    public NotificationModel(int notificationIdType, int referenceId, String referenceName, String Message, Date createdDate) {
        this.notificationTypeId = notificationIdType;
        this.referenceId = referenceId;
        this.referenceName = referenceName;
        this.message = Message;
        this.createdDate = createdDate;
    }


    public int getNotificationTypeId() { return notificationTypeId; }
    public int getReferenceId() { return referenceId; }
    public String getReferenceName() { return referenceName; }
    public String getMessage() { return message; }
    public Date getcreatedDate() { return createdDate; }

}
