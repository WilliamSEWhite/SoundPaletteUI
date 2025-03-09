package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TagApiEndpoints {
    /** gets global tag list */
    @GET("api/tag/get-tags")
    Call<List<TagModel>> getTags();

    /** pulls user tags */
    @GET("api/tag/get-user-tags/{id}")
    Call<List<TagModel>> getUserTags(@Path("id") int id);
    /** updates user tags */
    @POST("api/tag/update-user-tags/{id}")
    Call<ResponseBody> saveTags(@Path("id") int id, @Body List<TagModel> selectedTags);

}
