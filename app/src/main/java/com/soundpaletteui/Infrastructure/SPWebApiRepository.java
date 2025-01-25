package com.soundpaletteui.Infrastructure;

import android.util.Log;

import com.soundpaletteui.Infrastructure.ApiClients.LoginRegisterClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SPWebApiRepository {
    private static final String BASE_URL = "http://10.0.2.2:14509/";
    private static final String LOG_TAG = SPWebApiRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static SPWebApiRepository sInstance;

    private static Retrofit retrofit;

    //global access to room database
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
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static LoginRegisterClient getLoginRegisterClient(){
        return new LoginRegisterClient(retrofit);
    }

}
