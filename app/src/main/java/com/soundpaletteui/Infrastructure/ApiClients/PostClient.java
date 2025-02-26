package com.soundpaletteui.Infrastructure.ApiClients;

import android.util.Log;

import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.PostModel;
import com.soundpaletteui.Infrastructure.ApiEndpoints.PostApiEndpoints;

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
        Call<List<PostModel>> call = postApiEndpoints.getPosts();
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

}
