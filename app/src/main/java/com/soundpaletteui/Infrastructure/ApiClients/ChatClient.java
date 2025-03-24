package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.ChatApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.LocationApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.ChatMessageModel;
import com.soundpaletteui.Infrastructure.Models.ChatroomModel;
import com.soundpaletteui.Infrastructure.Models.NewChatroomModel;
import com.soundpaletteui.Infrastructure.Models.NewMessageModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatClient {
    private static ChatApiEndpoints apiEndpoints;
    public ChatClient(Retrofit retrofit) {
        apiEndpoints = retrofit.create(ChatApiEndpoints.class);
    }

    public List<ChatroomModel> getChatrooms() throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<List<ChatroomModel>> call = apiEndpoints.getChatrooms(userId);
        Response<List<ChatroomModel>> response = call.execute();

        return response.body();
    }

    public List<ChatMessageModel> getMessagesForChatroom(int chatroomId) throws IOException {
        Call<List<ChatMessageModel>> call = apiEndpoints.getMessagesForChatroom(chatroomId);
        Response<List<ChatMessageModel>> response = call.execute();

        return response.body();
    }
    public ChatroomModel getPrivateChatroom(String username) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<ChatroomModel> call = apiEndpoints.getPrivateChatroom(userId, username);
        Response<ChatroomModel> response = call.execute();

        return response.body();
    }
    public ChatroomModel createChatroom(NewChatroomModel newChatroom) throws IOException {
        Call<ChatroomModel> call = apiEndpoints.createChatroom(newChatroom);
        Response<ChatroomModel> response = call.execute();

        return response.body();
    }
    public Void sendMessage(NewMessageModel newMessage) throws IOException {
        Call<Void> call = apiEndpoints.sendMessage(newMessage);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void removeUserFromChatroom(int chatroomId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = apiEndpoints.removeUserFromChatroom(chatroomId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
}
