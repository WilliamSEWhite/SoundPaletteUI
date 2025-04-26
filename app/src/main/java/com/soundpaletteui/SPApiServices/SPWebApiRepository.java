package com.soundpaletteui.SPApiServices;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.LocationClient;
import com.soundpaletteui.SPApiServices.ApiClients.LoginRegisterClient;
import com.soundpaletteui.SPApiServices.ApiClients.NotificationClient;
import com.soundpaletteui.SPApiServices.ApiClients.PostClient;
import com.soundpaletteui.SPApiServices.ApiClients.PostInteractionClient;
import com.soundpaletteui.SPApiServices.ApiClients.TagClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.SPApiServices.ApiClients.FileClient;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Manages API client instances for SoundPalette

public class SPWebApiRepository {
    private static final String BASE_URL = "http://10.0.2.2:14509/";
    private static final String LOG_TAG = SPWebApiRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static SPWebApiRepository sInstance;
    private static Retrofit retrofit;

    // Returns the shared instance of SPWebApiRepository
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
    }

    // Sets up Retrofit with custom GSON and timeout settings
    private SPWebApiRepository(){
        // Get GSON to accept dates as String instead of Date (stops app from crashing)
        // API server doesn't like to accept Date, and I don't want to troubleshoot anymore
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // custom format for date parsing
                .create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    // Returns a LoginRegisterClient
    public static LoginRegisterClient getLoginRegisterClient() {
        return new LoginRegisterClient(retrofit);
    }

    // Returns a UserClient
    public static UserClient getUserClient() {
        return new UserClient(retrofit);
    }

    // Returns a LocationClient
    public static LocationClient getLocationClient() {
        return new LocationClient(retrofit);
    }

    // Returns a PostClient
    public static PostClient getPostClient() {
        return new PostClient(retrofit);
    }

    // Returns a TagClient
    public static TagClient getTagClient() {
        return new TagClient(retrofit);
    }

    // Returns a PostInteractionClient
    public static PostInteractionClient getPostInteractionClient() {
        return new PostInteractionClient(retrofit);
    }

    // Returns a ChatClient
    public static ChatClient getChatClient() {
        return new ChatClient(retrofit);
    }

    // Returns a FileClient
    public static FileClient getFileClient() {
        return new FileClient(retrofit);
    }

    // Returns a NotificationClient
    public static NotificationClient getNotificationClient() {
        return new NotificationClient(retrofit);
    }
}
