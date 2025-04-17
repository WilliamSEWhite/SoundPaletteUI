package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.Post.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PostApiEndpoints {
    @POST("api/post/create-post")
    Call<Void> createPost(@Body NewPostModel newPost);

    @GET("api/post/get-feed")
    Call<List<PostModel>> getPosts(@Query("userId") int userId);
    @GET("api/post/get-user-posts")
    Call<List<PostModel>> getPostsForUser(@Query("userId") int userId);

    @GET("api/post/get-username-posts")
    Call<List<PostModel>> getPostsForUsername(@Query("userId") int userId, @Query("username") String username);
    @GET("api/post/get-tagged-username-posts")
    Call<List<PostModel>> getTaggedPostsForUsername(@Query("userId") int userId, @Query("username") String username);
    @GET("api/post/get-following-posts")
    Call<List<PostModel>> getFollowingPosts(@Query("userId") int userId);

    @GET("api/post/get-user-saved-posts")
    Call<List<PostModel>> getUserSavedPosts(@Query("userId") int userId);

    @GET("api/post/delete-post")
    Call<Void> deletePost(@Query("postId") int postId, @Query("userId") int userId);
    @GET("api/post/get-trending-posts")
    Call<List<PostModel>> getTrendingPosts(@Query("userId") int userId, @Query("range") String range);
    @GET("api/post/get-posts-by-tag")
    Call<List<PostModel>> getPostsByTag(@Query("userId") int userId, @Query("tagId") int tagId);
    @GET("api/post/search-posts")
    Call<List<PostModel>> searchPosts(@Query("userId") int userId, @Query("searchTerm") String searchTerm);


}