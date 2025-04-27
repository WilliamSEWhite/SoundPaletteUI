package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;
import com.google.type.DateTime;

import java.util.Date;

public class FileModel {
    @SerializedName("ByteArrayContent")
    private String ByteArrayContent;
    @SerializedName("FileTypeId")
    private int FileTypeId;
    @SerializedName("UserId")
    private int UserId;
    @SerializedName("FileName")
    private String FileName;
    @SerializedName("FileUrl")
    private String FileUrl;
    @SerializedName("CreatedDate")
    private Date CreatedDate;
    @SerializedName("PublishDate")
    private Date PublishDate;
    @SerializedName("IsActive")
    private boolean IsActive = true;

    public FileModel(int userId, String fileName, int fileTypeId, String fileUrl) {
        UserId = userId;
        FileName = fileName;
        FileTypeId = fileTypeId;
        FileUrl = fileUrl;
    }

    public FileModel(String byteArrayContent, int userId, String fileName, int fileTypeId, String fileUrl) {
        ByteArrayContent = byteArrayContent;
        UserId = userId;
        FileName = fileName;
        FileTypeId = fileTypeId;
        FileUrl = fileUrl;
    }

    public FileModel(int userId, String fileName, int fileTypeId) {
        UserId = userId;
        FileName = fileName;
        FileTypeId = fileTypeId;
    }

    public FileModel(int fileTypeId, int userId, String fileName, String fileUrl, Date createdDate, Date publishDate, boolean isActive) {
        FileTypeId = fileTypeId;
        UserId = userId;
        FileName = fileName;
        CreatedDate = createdDate;
        PublishDate = publishDate;
        IsActive = isActive;
    }

    public String getByteArrayContent() { return ByteArrayContent; }
    public int getFileTypeId() { return FileTypeId; }
    public int getUserId() { return UserId; }
    public String getFileName() { return FileName; }
    public String getFileUrl() { return FileUrl; }
    public boolean getIsActive() { return IsActive; }

}
