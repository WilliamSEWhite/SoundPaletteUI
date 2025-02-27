package com.soundpaletteui.Infrastructure.ApiClients;

import android.util.Log;

import com.soundpaletteui.Infrastructure.ApiEndpoints.CommentApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.CommentModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentClient {
    private static CommentApiEndpoints postApiEndpoints;

    public CommentClient(Retrofit retrofit) {
        postApiEndpoints = retrofit.create(CommentApiEndpoints.class);
    }

    public Void makeComment(CommentModel newComment) throws IOException {
        Call<Void> call = postApiEndpoints.createComment(newComment);
        Response<Void> response = call.execute();
        return response.body();
    }

    public List<CommentModel> getComments(int postId) throws IOException {
        Call<List<CommentModel>> call = postApiEndpoints.getComments(postId);
        Response<List<CommentModel>> response = call.execute();
        return new ArrayList<>();
    }
}
