package com.soundpaletteui.Activities.Interactions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.SPApiServices.ApiClients.PostInteractionClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Post.CommentModel;
import com.soundpaletteui.Infrastructure.Models.Post.NewPostCommentModel;
import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Displays all of the comments in a post
public class CommentBottomSheet extends BottomSheetDialogFragment {
    private MainContentAdapter mainContentAdapter;
    private List<UserModel> userList;
    private UserModel user;
    private UserClient userClient;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<CommentModel> commentList;
    private EditText commentInput;
    private Button sendButton;
    private TextView noCommentsMessage;
    private int postId;
    private int userId;

    private OnCommentAddedListener commentAddedListener;

    public static CommentBottomSheet newInstance(int postId) {
        CommentBottomSheet fragment = new CommentBottomSheet();
        Bundle args = new Bundle();
        args.putInt("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_comments, container, false);
        initComponents(view);

        // Set the PostId to global variable
        postId = getArguments().getInt("postId", -1);

        // Find the recyclerView
        recyclerView = view.findViewById(R.id.comment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // TextView for displaying if there are no comments
        noCommentsMessage = view.findViewById(R.id.noCommentsDisplay);

        // Create and get a list of all the comments based on the PostId
        commentList = new ArrayList<>();
        adapter = new CommentAdapter(requireActivity(), commentList, () -> dismiss());
        recyclerView.setAdapter(adapter);

        // Find the comment input box and send button
        commentInput = view.findViewById(R.id.comment_textbox);
        sendButton = view.findViewById(R.id.comment_send);

        // Fetch comments from API
        new FetchCommentsAsync().execute();

        // Send button listener
        // Gets the comment message and pushes a new comment record to the database.
        sendButton.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            commentInput.clearFocus();
            commentInput.setText("");
            if (!text.isEmpty()) {
                new PostCommentAsync().execute(new NewPostCommentModel(user.getUserId(), postId, text));
                if (commentList.size() > 1) {
                    recyclerView.smoothScrollToPosition(commentList.size() - 1);
                }
                new FetchCommentsAsync().execute();
            }
        });

        return view;
    }

    // Makes a call to the API server to get the list of comments based on PostId
    private class FetchCommentsAsync extends AsyncTask<Void, Void, List<CommentModel>> {
        @Override
        protected List<CommentModel> doInBackground(Void... voids) {
            List<CommentModel> fetchedComments = new ArrayList<>();
            try {
                PostInteractionClient postInteractionClient = SPWebApiRepository.getInstance().getPostInteractionClient();
                fetchedComments = postInteractionClient.getCommentsForPost(postId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fetchedComments;
        }

        // After fetching the list of comments, display them in a CommentAdapter
        @Override
        protected void onPostExecute(List<CommentModel> comments) {
            commentList.clear();
            commentList.addAll(comments);
            adapter.notifyDataSetChanged();

            // If there are no comments, then display the "no comments" message
            if (comments.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noCommentsMessage.setVisibility(View.VISIBLE);
                noCommentsMessage.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start();
            // else, display the comment(s)
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noCommentsMessage.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> noCommentsMessage.setVisibility(View.GONE))
                        .start();
            }
        }
    }

    // Create a NewPostCommentModel and send the information to API Server
    private class PostCommentAsync extends AsyncTask<NewPostCommentModel, Void, Void> {
        @Override
        protected Void doInBackground(NewPostCommentModel... newComment) {
            try {
                PostInteractionClient postInteractionClient = SPWebApiRepository.getInstance().getPostInteractionClient();
                postInteractionClient.postComment(newComment[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Increment post counter
        @Override
        protected void onPostExecute(Void v) {
            new FetchUpdatedCommentCountTask().execute(postId);
        }
    }

    // Get a user client
    private void initComponents(View view) {
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
    }

    // Sets the display for comment count
    public interface OnCommentAddedListener {
        void onCommentCountUpdated(int newCount);
    }

    // Creates a listen for when a new comment is added
    public void setOnCommentAddedListener(OnCommentAddedListener listener) {
        this.commentAddedListener = listener;
    }

    // Get the initial comment count
    private class FetchUpdatedCommentCountTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            int postId = params[0];
            try {
                PostInteractionClient client = SPWebApiRepository.getInstance().getPostInteractionClient();
                return client.getCommentsForPost(postId).size();
            } catch (IOException e) {
                return -1;
            }
        }

        // Update the counter when a new comment is created
        @Override
        protected void onPostExecute(Integer updatedCount) {
            if (commentAddedListener != null && updatedCount >= 0) {
                commentAddedListener.onCommentCountUpdated(updatedCount);
            }
        }
    }
}