package com.soundpaletteui.Infrastructure.Models.Post;

import com.google.gson.annotations.SerializedName;

public class PostContentModel {
    @SerializedName("PostTextContent")

    private String postTextContent;
    @SerializedName("BackgroundColour")
    private String backgroundColour;

    @SerializedName("FontColour")
    private String fontColour;

    public PostContentModel(String postTextContent, String backgroundColour, String fontColour) {
        this.postTextContent = postTextContent;
        this.backgroundColour = backgroundColour;
        this.fontColour = fontColour;

    }

    public String getPostTextContent(){
        return postTextContent;
    }
    public String getBackgroundColour(){
        return backgroundColour;
    }
    public String getFontColour(){
        return fontColour;
    }

}
