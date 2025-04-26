package com.soundpaletteui.SPApiServices.ApiClients;

import android.util.Log;

import com.google.gson.Gson;
import com.soundpaletteui.Infrastructure.Models.Post.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.SPApiServices.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// Handles API requests for creating, retrieving, updating, deleting, and searching posts in SoundPalette.

public class PostClient {
    private static PostApiEndpoints postApiEndpoints;

    public PostClient(Retrofit retrofit) {
        postApiEndpoints = retrofit.create(PostApiEndpoints.class);
    }

    // Creates and sends a post with a file (image/audio) to API Server
    public void createFilePost(MultipartBody.Part filePart, RequestBody metaData, Callback<Integer> callback) {
        Call<Integer> call = postApiEndpoints.createFilePost(filePart, metaData);
        call.enqueue(callback);
    }

    // Creates a text post and sends it to API Server
    public Void makePost(NewPostModel newPost) throws IOException {

        Call<Void> call = postApiEndpoints.createPost(newPost);
        Response<Void> response = call.execute();

        return response.body();
    }

    // Gets a list of PostModels
    public List<PostModel> getPosts(int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPosts(userId, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Loads one specific PostModel
    public PostModel getPost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<PostModel> call = postApiEndpoints.getPost(userId, postId);
        Response<PostModel> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return null;
        }

        PostModel post = response.body();
        if (post == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return null;
        }

        return post;
    }

    // Gets a list of PostModels based on the user
    public List<PostModel> getPostsForUser(int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPostsForUser(userId, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of saved posts based on the user
    public List<PostModel> getSavedPostsForUser(int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getUserSavedPosts(userId, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of PostModels based on the username
    public List<PostModel> getPostsForUsername(String username, int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPostsForUsername(userId, username, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of all tagged posts based on the username
    public List<PostModel> getTaggedPostsForUsername(String username, int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getTaggedPostsForUsername(userId, username, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of PostModels based on the followed users
    public List<PostModel> getFollowingPosts(int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getFollowingPosts(userId, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of the most liked posts based on a time period
    public List<PostModel> getTrendingPosts(String range, int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getTrendingPosts(userId, range, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of PostModels based on PostTags
    public List<PostModel> getPostsByTag(int tagId, int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPostsByTag(userId, tagId, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Gets a list of PostModels based on a search Term
    public List<PostModel> searchPosts(String searchTerm, int page) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.searchPosts(userId, searchTerm, page);
        Response<List<PostModel>> response = call.execute();

        if (!response.isSuccessful()) {
            Log.e("PostClient", "Error fetching posts: " + response.code() + " - " + response.message());
            return new ArrayList<>();
        }

        List<PostModel> posts = response.body();
        if (posts == null) {
            Log.w("PostClient", "Received null response body for posts.");
            return new ArrayList<>();
        }

        Log.d("PostClient", "Fetched " + posts.size() + " posts.");
        return posts;
    }

    // Delete a specific Post
    public Void deletePost(int postId, int userId) throws IOException {

        Call<Void> call = postApiEndpoints.deletePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }

    // Update the details of a specific post
    public Void updatePost(PostModel updatedPost) throws IOException {
        Log.d("PostClient", "caption: " + updatedPost.getPostCaption());
        Call<Void> call = postApiEndpoints.updatePost(updatedPost);
        Gson gson = new Gson();
        Log.d("PostClient", "Sending PostModel JSON: " + gson.toJson(updatedPost));
        Response<Void> response = call.execute();
        return response.body();
    }

}
