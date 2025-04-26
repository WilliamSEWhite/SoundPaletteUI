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

// Handles API calls for post interactions like commenting, liking, and saving posts

public class PostInteractionClient {
    private static PostInteractionApiEndpoints postInteractionApiEndpoints;

    public PostInteractionClient(Retrofit retrofit) {
        postInteractionApiEndpoints = retrofit.create(PostInteractionApiEndpoints.class);
    }

    // Sends a new comment to the API server
    public Void postComment(NewPostCommentModel newComment) throws IOException {

        Call<Void> call = postInteractionApiEndpoints.postComment(newComment);
        Response<Void> response = call.execute();

        return response.body();
    }

    // Gets a list of comments based on the postId
    public List<CommentModel> getCommentsForPost(int postId) throws IOException {

        Call<List<CommentModel>> call = postInteractionApiEndpoints.getPostComments(postId);
        Response<List<CommentModel>> response = call.execute();
        return response.body();
    }

    // Likes a post for the current user
    public Void likePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.likePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }

    // Removes a like from a post for the current user
    public Void unlikePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.unlikePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }

    // Saves a post for the current user
    public Void savePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.savePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }

    // Removes a saved post for the current user
    public Void unsavePost(int postId) throws IOException {
        int userId = AppSettings.getInstance().getUserId();

        Call<Void> call = postInteractionApiEndpoints.unsavePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
}
