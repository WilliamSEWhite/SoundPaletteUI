package com.soundpaletteui.SPApiServices.ApiClients;

import android.util.Log;

import com.soundpaletteui.Infrastructure.Models.Post.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.Post.PostModel;
import com.soundpaletteui.SPApiServices.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostClient {
    private static PostApiEndpoints postApiEndpoints;

    public PostClient(Retrofit retrofit) {
        postApiEndpoints = retrofit.create(PostApiEndpoints.class);
    }

    public Void makePost(NewPostModel newPost) throws IOException {

        Call<Void> call = postApiEndpoints.createPost(newPost);
        Response<Void> response = call.execute();

        return response.body();
    }

    public List<PostModel> getPosts() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPosts(userId);
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

    public List<PostModel> getPostsForUser() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPostsForUser(userId);
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
    public List<PostModel> getSavedPostsForUser() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getUserSavedPosts(userId);
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
    public List<PostModel> getPostsForUsername(String username) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getPostsForUsername(userId, username);
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
    public List<PostModel> getTaggedPostsForUsername(String username) throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getTaggedPostsForUsername(userId, username);
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
    public List<PostModel> getFollowingPosts() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getFollowingPosts(userId);
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

    public List<PostModel> getTrendingPosts() throws IOException {
        int userId = AppSettings.getInstance().getUserId();
        Call<List<PostModel>> call = postApiEndpoints.getTrendingPosts(userId);
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

    public Void deletePost(int postId, int userId) throws IOException {

        Call<Void> call = postApiEndpoints.deletePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }

    public Void updatePost(PostModel updatedPost) throws IOException {
        Call<Void> call = postApiEndpoints.updatePost(updatedPost);
        Response<Void> response = call.execute();
        return response.body();
    }

}
