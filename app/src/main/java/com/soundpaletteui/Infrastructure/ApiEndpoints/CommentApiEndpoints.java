package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.CommentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentApiEndpoints {
    @POST("api/comment/create-comment")
    Call<Void> createComment(@Body CommentModel newComment);

    @GET("api/comment/get-comments/{postId}")
    Call<List<CommentModel>> getComments(@Path("postId") int postId);
}