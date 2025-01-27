package com.soundpaletteui.Main.Activities.apiTest.apiClients;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginApiClient {

    private static final String BASE_URL = "http://10.0.2.2:14509/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if(retrofit==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Logs request & response bodies

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
