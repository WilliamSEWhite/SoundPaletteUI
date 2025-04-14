package com.soundpaletteui.SPApiServices.ApiClients;

import com.soundpaletteui.SPApiServices.ApiEndpoints.LocationApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.LocationModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocationClient {
    private static LocationApiEndpoints apiEndpoints;
    public LocationClient(Retrofit retrofit) {
        apiEndpoints = retrofit.create(LocationApiEndpoints.class);
    }

    public List<LocationModel> getLocations() throws IOException {
        Call<List<LocationModel>> call = apiEndpoints.getLocations();
        Response<List<LocationModel>> response = call.execute();
        return response.body();
    }
}
