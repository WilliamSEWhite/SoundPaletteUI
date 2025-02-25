package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TagApiEndpoints {
    @GET("api/tag/get-tags")
    Call<List<TagModel>> getTags();

}
