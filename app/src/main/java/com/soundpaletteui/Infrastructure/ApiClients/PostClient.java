package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.UserEndpoints;

import retrofit2.Retrofit;

public class PostClient {
    private static PostApiEndpoints postApiEndpoints;

    public PostClient(Retrofit retrofit) {
        postApiEndpoints = retrofit.create(PostApiEndpoints.class);
    }

}
