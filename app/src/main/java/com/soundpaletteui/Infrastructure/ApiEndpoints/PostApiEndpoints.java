package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostApiEndpoints {
    @POST("api/post/create-post")
    Call<Void> createPost(@Body NewPostModel newPost);

    @GET("api/post/get-posts")
    Call<List<PostModel>> getPosts();

    @GET("api/post/delete-post")
    Call<Void> deletePost(@Query("postId") int postId, @Query("userId") int userId);

}