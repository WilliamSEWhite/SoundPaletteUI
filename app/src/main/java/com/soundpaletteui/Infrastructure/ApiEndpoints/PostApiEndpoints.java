package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostApiEndpoints {
    @POST("api/post/create-post")
    Call<Void> createPost(@Body NewPostModel newPost);

}
