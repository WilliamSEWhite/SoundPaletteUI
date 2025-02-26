package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PostApiEndpoints {
    @POST("api/post/create-post")
    Call<Void> createPost(@Body NewPostModel newPost);

    @GET("api/post/get-posts")
    Call<List<PostModel>> getPosts();
}