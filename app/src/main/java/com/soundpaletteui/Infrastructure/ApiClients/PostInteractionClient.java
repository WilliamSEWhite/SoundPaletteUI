package com.soundpaletteui.Infrastructure.ApiClients;

import com.soundpaletteui.Infrastructure.ApiEndpoints.PostApiEndpoints;
import com.soundpaletteui.Infrastructure.ApiEndpoints.PostInteractionApiEndpoints;
import com.soundpaletteui.Infrastructure.Models.CommentModel;
import com.soundpaletteui.Infrastructure.Models.NewPostCommentModel;
import com.soundpaletteui.Infrastructure.Models.NewPostModel;

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

    public Void makeComment(NewPostCommentModel newComment) throws IOException {

        Call<Void> call = postInteractionApiEndpoints.createComment(newComment);
        Response<Void> response = call.execute();

        return response.body();
    }
    public List<CommentModel> getCommentsForPost(int postId) throws IOException {

        Call<List<CommentModel>> call = postInteractionApiEndpoints.getPostComments(postId);
        Response<List<CommentModel>> response = call.execute();
        return response.body();
    }
    public Void likePost(int postId, int userId) throws IOException {

        Call<Void> call = postInteractionApiEndpoints.likePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }
    public Void unlikePost(int postId, int userId) throws IOException {

        Call<Void> call = postInteractionApiEndpoints.unlikePost(postId, userId);
        Response<Void> response = call.execute();

        return response.body();
    }

}
