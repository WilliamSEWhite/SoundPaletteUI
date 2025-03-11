package com.soundpaletteui.Activities.Interactions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.soundpaletteui.Infrastructure.Adapters.MainContentAdapter;
import com.soundpaletteui.Infrastructure.ApiClients.CommentClient;
import com.soundpaletteui.Infrastructure.ApiClients.PostInteractionClient;
import com.soundpaletteui.Infrastructure.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.CommentModel;
import com.soundpaletteui.Infrastructure.Models.NewPostCommentModel;
import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.AppSettings;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private int postId;
    private int userId;
    private CommentClient commentClient;

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

        postId = getArguments().getInt("postId", -1);

        recyclerView = view.findViewById(R.id.comment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        commentInput = view.findViewById(R.id.comment_textbox);
        sendButton = view.findViewById(R.id.comment_send);

        // Fetch comments from API
        new FetchCommentsAsync().execute();

        sendButton.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            commentInput.clearFocus();
            commentInput.setText("");
            if (!text.isEmpty()) {
                new PostCommentAsync().execute(new NewPostCommentModel(user.getUserId(), postId, text));
                if(commentList.size()>1){
                    recyclerView.smoothScrollToPosition(commentList.size() - 1);
                }
                new FetchCommentsAsync().execute();

            }
        });

        return view;
    }

    // AsyncTask to simulate fetching dummy comments
    private class FetchCommentsAsync extends AsyncTask<Void, Void, List<CommentModel>> {
        @Override
        protected List<CommentModel> doInBackground(Void... voids) {
            List<CommentModel> dummyComments = new ArrayList<>();

            try {
                PostInteractionClient postInteractionClient = SPWebApiRepository.getInstance().getPostInteractionClient();
                dummyComments = postInteractionClient.getCommentsForPost(postId);

            } catch (IOException e) {  }

            // Generate dummy comments
            return dummyComments;
        }

        @Override
        protected void onPostExecute(List<CommentModel> comments) {
            commentList.clear();
            commentList.addAll(comments);
            adapter.notifyDataSetChanged();
        }
    }
    private class PostCommentAsync extends AsyncTask<NewPostCommentModel, Void, Void> {
        @Override
        protected Void doInBackground(NewPostCommentModel... newComment) {

            try {
                PostInteractionClient postInteractionClient = SPWebApiRepository.getInstance().getPostInteractionClient();
                postInteractionClient.postComment(newComment[0]);

            } catch (IOException e) {  }

            // Generate dummy comments
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }


//    AsyncTask to fetch comments from API
//    private class FetchCommentsTask extends AsyncTask<Void, Void, List<CommentModel>> {
//        @Override
//        protected List<CommentModel> doInBackground(Void... voids) {
//            try {
//                return commentClient.getComments(postId);  // Fetch comments from API
//            } catch (IOException e) {
//                e.printStackTrace();
//                return new ArrayList<>();  // Return empty list on error
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<CommentModel> comments) {
//            if (comments.isEmpty()) {
//                Toast.makeText(getContext(), "No comments found.", Toast.LENGTH_SHORT).show();
//            } else {
//                commentList.clear();
//                commentList.addAll(comments);
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }

//    AsyncTask to send a new comment to API
//    private class SendCommentTask extends AsyncTask<CommentModel, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(CommentModel... params) {
//            try {
//                commentClient.makeComment(params[0]);
//                return true;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            if (success) {
//                new FetchCommentsTask().execute();  // Refresh comments after posting
//            } else {
//                Toast.makeText(getContext(), "Failed to send comment", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    // Initializes views and loads user data.
    private void initComponents(View view) {
        // Get arguments instead of Intent
        user = AppSettings.getInstance().getUser();
        userList = new ArrayList<>();
        mainContentAdapter = new MainContentAdapter(userList);
        userClient = SPWebApiRepository.getInstance().getUserClient();
    }
}
