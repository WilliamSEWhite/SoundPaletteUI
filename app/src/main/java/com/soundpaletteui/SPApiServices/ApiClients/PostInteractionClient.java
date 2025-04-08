package com.soundpaletteui.SPApiServices.ApiClients;

import com.soundpaletteui.SPApiServices.ApiEndpoints.PostInteractionApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.Post.CommentModel;
import com.soundpaletteui.Infrastructure.Models.Post.NewPostCommentModel;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostInteractionClient {
    private static PostInteractionApiEndpoints postInteractionApiEndpoints;

    public PostInteractionClient(Retrofit retrofit) {
        postInteractionApiEndpoints = retrofit.create(PostInteractionApiEndpoints.class);
    }

    public Void postComment(NewPostCommentModel newComment) throws IOException {

        Call<Void> call = postInteractionApiEndpoints.postComment(newComment);
        Response<Void> response = call.execute();

        return response.body();
    }
    public List<CommentModel> getCommentsForPost(int postId) throws IOException {

        Call<List<CommentModel>> call = postInteractionApiEndpoints.getPostComments(postId);
        Response<List<CommentModel>> response = call.execute();
        return response.body();
    }
    public Void likePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.likePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void unlikePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.unlikePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void savePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.savePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void unsavePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.unsavePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
}
