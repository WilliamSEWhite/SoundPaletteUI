package com.soundpaletteui.Infrastructure.Models.Post;

import com.google.gson.annotations.SerializedName;
import com.soundpaletteui.Infrastructure.Models.FileModel;

/** wrapper class for file metadata and post data */
public class PostFileModel {
    @SerializedName("Post")
    public NewPostModel Post;
    @SerializedName("File")
    public FileModel File;

    public PostFileModel(NewPostModel post, FileModel file) {
        Post = post;
        File = file;
    }
}
