package com.soundpaletteui.Infrastructure.ApiClients;

import com.google.gson.Gson;
import com.soundpaletteui.Infrastructure.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.UserEndpoints;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
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
}
