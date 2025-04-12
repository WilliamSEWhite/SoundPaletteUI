package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.LocationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LocationApiEndpoints {
    @GET("api/location/get-locations")
    Call<List<LocationModel>> getLocations();

}
