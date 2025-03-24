package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.UserInfoModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserEndpoints {
    @GET("api/user/get-user/{id}")
    Call<UserModel> getUser(@Path("id") int id);
    @POST("api/user/update-user-info/{id}")
    Call<UserModel> updateUserInfo(@Path("id") int id, @Body UserInfoModel userInfo);
    @POST("api/user/update-user-info")
    Call<UserInfoModel> updateUserInfo(@Body UserInfoModel userInfo);
    @GET("api/user/get-user-info/{id}")
    Call<UserInfoModel> getUserInfo(@Path("id") int id);
    @GET("api/user/get-user-profile/{id}")
    Call<UserProfileModel> getUserProfile(@Path("id") int id);
    @GET("api/user/get-user-profile-by-username/{username}")
    Call<UserProfileModel> getUserProfileByUsername(@Path("username") String username);
    @POST("api/user/update-user-profile")
    Call<UserProfileModel> updateUserProfile(@Body UserProfileModel userProfile);
    @GET("api/user/follow-user")
    Call<Void> followUser(@Query("followerId") int followerId, @Query("followingUsername") String followingUsername);
    @GET("api/user/unfollow-user")
    Call<Void> unfollowUser(@Query("followerId") int followerId, @Query("followingUsername") String followingUsername);
}
