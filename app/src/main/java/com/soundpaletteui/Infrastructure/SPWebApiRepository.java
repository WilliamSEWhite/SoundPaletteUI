package com.soundpaletteui.Infrastructure;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soundpaletteui.Infrastructure.ApiClients.LocationClient;
import com.soundpaletteui.Infrastructure.ApiClients.LoginRegisterClient;
import com.soundpaletteui.Infrastructure.ApiClients.PostClient;
import com.soundpaletteui.Infrastructure.ApiClients.PostInteractionClient;
import com.soundpaletteui.Infrastructure.ApiClients.TagClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SPWebApiRepository {
    private static final String BASE_URL = "http://10.0.2.2:14509/";
    private static final String LOG_TAG = SPWebApiRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static SPWebApiRepository sInstance;

    private static Retrofit retrofit;

    public static SPWebApiRepository getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new web api client instance");
                sInstance = new SPWebApiRepository();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        //return database instance
        return sInstance;
    }//end getInstance

    private SPWebApiRepository(){
        /* set GSON to accept dates as String instead of Date (stops app from crashing)
           API server doesn't like to accept Date, and I don't want to troubleshoot anymore  */
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // custom format for date parsing
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static LoginRegisterClient getLoginRegisterClient(){
        return new LoginRegisterClient(retrofit);
    }
    public static UserClient getUserClient(){
        return new UserClient(retrofit);
    }
    public static LocationClient getLocationClient(){
        return new LocationClient(retrofit);
    }
    public static PostClient getPostClient(){
        return new PostClient(retrofit);
    }

    public static TagClient getTagClient(){
        return new TagClient(retrofit);
    }

    public static PostInteractionClient getPostInteractionClient(){
        return new PostInteractionClient(retrofit);
    }

}
