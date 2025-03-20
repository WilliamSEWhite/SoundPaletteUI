package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MessageModel {
    @SerializedName("MessageId")
    private int messageId;
    @SerializedName("Message")
    private String message;
    @SerializedName("SentBy")
    private String sentBy;
    @SerializedName("SentDate")
    private Date sentDate;

    public MessageModel(int messageId, String message, String sentBy, Date sentDate)
    {
        this.messageId = messageId;
        this.message = message;
        this.sentBy = sentBy;
        this.sentDate = sentDate;
    }

    public int getMessageId() { return messageId; };
    public String getMessage() { return message; };
    public String getSentBy() { return sentBy; };
    public Date getSentDate() { return sentDate; };
}
