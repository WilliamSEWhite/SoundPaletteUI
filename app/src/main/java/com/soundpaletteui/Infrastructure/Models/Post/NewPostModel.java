package com.soundpaletteui.Infrastructure.Models.Post;

import com.google.gson.annotations.SerializedName;
import com.soundpaletteui.Infrastructure.Models.TagModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewPostModel {
    @SerializedName("UserId")
    public int userId;
    @SerializedName("PostTypeId")
    public int postTypeId;
    @SerializedName("Caption")
    public String caption;
    @SerializedName("IsMature")
    public boolean isMature;

    @SerializedName("IsPremium")
    public boolean isPremium;
    @SerializedName("CreatedDate")
    public Date createdDate;
    @SerializedName("PublishDate")
    public Date publishDate;

    @SerializedName("PostTags")
    public ArrayList<TagModel> postTags;
    @SerializedName("PostUserTags")
    public List<String> postUserTags;
    @SerializedName("PostContent")
    public PostContentModel postContentModel;

    public NewPostModel(int userId, int postTypeId,String caption, boolean isPremium, boolean isMature, Date createdDate, Date publishDate, ArrayList<TagModel> postTags, PostContentModel postContentModel, List<String> postUserTags) {
        this.userId = userId;
        this.postTypeId = postTypeId;
        this.caption = caption;
        this.isPremium = isPremium;
        this.isMature = isMature;
        this.createdDate = createdDate;
        this.publishDate = publishDate;
        this.postTags = postTags;
        this.postContentModel = postContentModel;
        this.postUserTags = postUserTags;
    }
}