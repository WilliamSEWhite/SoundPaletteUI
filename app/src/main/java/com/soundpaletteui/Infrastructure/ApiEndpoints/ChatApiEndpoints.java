package com.soundpaletteui.Infrastructure.ApiEndpoints;

import com.soundpaletteui.Infrastructure.Models.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.NewMessageModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApiEndpoints {
    @GET("api/chat/get-user-chatrooms")
    Call<List<ChatroomModel>> getChatrooms(@Query("userId") int userId);
    @GET("api/chat/get-messages-for-chatroom")
    Call<List<ChatMessageModel>> getMessagesForChatroom(@Query("chatroomId") int chatroomId);
    @GET("api/chat/get-private-chatroom")
    Call<ChatroomModel> getPrivateChatroom(@Query("userId") int userId, @Query("username") String username);
    @POST("api/chat/create-chatroom")
    Call<ChatroomModel> createChatroom(@Body NewChatroomModel newChatRoom);
    @POST("api/chat/send-message")
    Call<Void> sendMessage(@Body NewMessageModel newMessage);
    @GET("api/chat/remove-user-from-chat")
    Call<Void> removeUserFromChatroom(@Query("chatroomId") int chatroomId, @Query("userId") int userId);

}
