package com.soundpaletteui.Activities.Interactions;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.soundpaletteui.Infrastructure.Models.CommentModel;
import com.soundpaletteui.R;

import java.util.ArrayList;
        import java.util.List;

public class CommentBottomSheet extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<CommentModel> commentList;
    private EditText commentInput;
    private Button sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_comments);

        // Retrieve postId from Intent
        int postId = getIntent().getIntExtra("postId", -1); // Default to -1 if not found
        int userId = getIntent().getIntExtra("userId", -1); // Default to -1 if not found

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.comment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        // Input and send button
        commentInput = findViewById(R.id.comment_textbox);
        sendButton = findViewById(R.id.comment_send);

        // Handle adding a comment
        sendButton.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            if (!text.isEmpty()) {
                adapter.addComment(new CommentModel(postId, userId, text));
                commentInput.setText(""); // Clear input field
                recyclerView.smoothScrollToPosition(commentList.size() - 1);
            }
        });

        // Show Bottom Sheet Dialog (optional)
        showBottomSheet();
    }


    private void showBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_comments, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}
