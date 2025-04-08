package com.soundpaletteui.Infrastructure.Models.Chat;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChatMessageModel {
    @SerializedName("MessageId")
    private int messageId;
    @SerializedName("Message")
    private String message;
    @SerializedName("SentBy")
    private String sentBy;
    @SerializedName("SentDate")
    private Date sentDate;

    @SerializedName("IsActiveUser")
    private boolean isActiveUser;

    public ChatMessageModel(int messageId, String message, String sentBy, Date sentDate, boolean isActiveUser)
    {
        this.messageId = messageId;
        this.message = message;
        this.sentBy = sentBy;
        this.sentDate = sentDate;
        this.isActiveUser = isActiveUser;
    }

    public int getMessageId() { return messageId; };
    public String getMessage() { return message; };
    public String getSentBy() { return sentBy; };
    public Date getSentDate() { return sentDate; };
    public boolean getIsActiveUser() { return isActiveUser; };

}
