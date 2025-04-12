package com.soundpaletteui.Infrastructure.Models.Post;

import com.google.gson.annotations.SerializedName;

public class PostContentModel {
    @SerializedName("PostTextContent")

    private String postTextContent;

    public PostContentModel(String postTextContent) {
        this.postTextContent = postTextContent;
    }

    public String getPostTextContent(){
        return postTextContent;
    }

}
