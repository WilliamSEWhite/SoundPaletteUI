package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.TagApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.TagModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TagClient {
    private static TagApiEndpoints apiEndpoints;
    public TagClient(Retrofit retrofit) {
        apiEndpoints = retrofit.create(TagApiEndpoints.class);
    }

    public List<TagModel> getTags() throws IOException {
        Call<List<TagModel>> call = apiEndpoints.getTags();
        Response<List<TagModel>> response = call.execute();
        return response.body();
    }
}
