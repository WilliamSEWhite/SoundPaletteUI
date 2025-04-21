package com.soundpaletteui.SPApiServices.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationModel;
import com.soundpaletteui.Infrastructure.Models.Notifications.NotificationSettingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationApiEndpoints {
    @GET("api/notification/get-notifications")
    Call<List<NotificationModel>> getNotifications(@Query("userId") int userId);
    @GET("api/notification/get-notification-flag")
    Call<Boolean> getNotificationFlag(@Query("userId") int userId);
    @GET("api/notification/get-device-notification-flag")
    Call<Boolean> getDeviceNotificationFlag(@Query("userId") int userId);
    @GET("api/notification/get-message-flag")
    Call<Boolean> getMessageFlag(@Query("userId") int userId);
    @GET("api/notification/get-notification-settings")
    Call<List<NotificationSettingModel>> getNotificationSettings(@Query("userId") int userId);
    @POST("api/notification/set-notification-settings")
    Call<Void> setNotificationSettings(@Body List<NotificationSettingModel> settings);

    @GET("api/notification/has-notification")
    Call<Boolean> hasNotification(@Query("userId") int userId);
    @GET("api/notification/has-message")
    Call<Boolean> hasMessage(@Query("userId") int userId);

}
