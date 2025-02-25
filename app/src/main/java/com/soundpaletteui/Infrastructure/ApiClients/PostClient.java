package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.UserEndpoints;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;
import com.soundpaletteui.Infrastructure.Models.TagModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostClient {
    private static PostApiEndpoints postApiEndpoints;

    public PostClient(Retrofit retrofit) {
        postApiEndpoints = retrofit.create(PostApiEndpoints.class);
    }

    public Object makePost(NewPostModel newPost) throws IOException {

        Call<Object> call = postApiEndpoints.createPost(newPost);
        Response<Object> response = call.execute();
        return response.body();
    }
}
