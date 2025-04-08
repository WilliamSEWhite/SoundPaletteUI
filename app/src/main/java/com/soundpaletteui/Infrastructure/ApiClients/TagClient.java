package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.TagApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TagClient {
    private static TagApiEndpoints tagEndpoints;
    public TagClient(Retrofit retrofit) {
        tagEndpoints = retrofit.create(TagApiEndpoints.class);
    }

    /** get global tag list */
    public List<TagModel> getTags() throws IOException {
        Call<List<TagModel>> call = tagEndpoints.getTags();
        Response<List<TagModel>> response = call.execute();
        return response.body();
    }

    /** get user tag list */
    public List<TagModel> getUserTags(int id) throws IOException {
        System.out.println("getUserTags");
        Call<List<TagModel>> call = tagEndpoints.getUserTags(id);
        Response<List<TagModel>> response = call.execute();
        return response.body();
    }

    /** update user tags */
    public Response<ResponseBody> updateTags(int id, List<TagModel> selectedTags) throws IOException {
        Call<ResponseBody> call = tagEndpoints.saveTags(id, selectedTags);
        return call.execute();
    }

    public List<TagModel> searchTags(String searchTerm) throws IOException {
        Call<List<TagModel>> call = tagEndpoints.searchTags(searchTerm);
        Response<List<TagModel>> response = call.execute();
        return response.body();
    }
}
