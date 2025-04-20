package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.Post.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostApiEndpoints {

    @Multipart
    @POST("/api/post/create-file-post")
    Call<Integer> createFilePost(
            @Part MultipartBody.Part file,
            @Part("MetaData") RequestBody metaData
    );
    @POST("api/post/create-post")
    Call<Void> createPost(@Body NewPostModel newPost);

    @GET("api/post/get-feed")
    Call<List<PostModel>> getPosts(@Query("userId") int userId, @Query("page") int page);
    @GET("api/post/get-user-posts")
    Call<List<PostModel>> getPostsForUser(@Query("userId") int userId, @Query("page") int page);

    @GET("api/post/get-username-posts")
    Call<List<PostModel>> getPostsForUsername(@Query("userId") int userId, @Query("username") String username, @Query("page") int page);
    @GET("api/post/get-tagged-username-posts")
    Call<List<PostModel>> getTaggedPostsForUsername(@Query("userId") int userId, @Query("username") String username, @Query("page") int page);
    @GET("api/post/get-following-posts")
    Call<List<PostModel>> getFollowingPosts(@Query("userId") int userId, @Query("page") int page);

    @GET("api/post/get-user-saved-posts")
    Call<List<PostModel>> getUserSavedPosts(@Query("userId") int userId, @Query("page") int page);

    @GET("api/post/delete-post")
    Call<Void> deletePost(@Query("postId") int postId, @Query("userId") int userId);
    @GET("api/post/get-trending-posts")
    Call<List<PostModel>> getTrendingPosts(@Query("userId") int userId, @Query("range") String range, @Query("page") int page);
    @GET("api/post/get-posts-by-tag")
    Call<List<PostModel>> getPostsByTag(@Query("userId") int userId, @Query("tagId") int tagId, @Query("page") int page);
    @GET("api/post/search-posts")
    Call<List<PostModel>> searchPosts(@Query("userId") int userId, @Query("searchTerm") String searchTerm, @Query("page") int page);


    @POST("api/post/update-post")
    Call<Void> updatePost(@Body PostModel updatedPost);
    @GET("api/post/get-post")
    Call<PostModel> getPost(@Query("userId") int userId, @Query("postId") int postId);


}