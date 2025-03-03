package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.CommentModel;
import com.soundpaletteui.Infrastructure.Models.NewPostCommentModel;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostInteractionApiEndpoints {
    @POST("api/postInteraction/create-comment")
    Call<Void> createComment(@Body NewPostCommentModel newComment);
    @GET("api/postInteraction/get-post-comments")
    Call<List<CommentModel>> getPostComments(@Query("postId") int postId);
    @GET("api/postInteraction/like-post")
    Call<Void> likePost(@Query("postId") int postId, @Query("userId") int userId);
    @GET("api/postInteraction/unlike-post")
    Call<Void> unlikePost(@Query("postId") int postId, @Query("userId") int userId);


}
