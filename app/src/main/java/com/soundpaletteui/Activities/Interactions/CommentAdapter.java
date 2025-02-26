package com.soundpaletteui.Activities.Interactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.CommentModel;
import com.soundpaletteui.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentModel> commentList;

    public CommentAdapter(List<CommentModel> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);
        //holder.postId.setText(comment.getPostId());
        ///holder.userId.setText(comment.getUserId());
        holder.message.setText(comment.getMessage());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void addComment(CommentModel comment) {
        commentList.add(comment);
        notifyItemInserted(commentList.size() - 1);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username, message;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.adapter_comment_user);
            message = itemView.findViewById(R.id.adapter_comment_msg);
        }
    }
}
