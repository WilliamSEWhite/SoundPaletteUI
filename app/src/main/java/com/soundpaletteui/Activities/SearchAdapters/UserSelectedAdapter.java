package com.soundpaletteui.Activities.SearchAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.UserModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModel;
import com.soundpaletteui.Infrastructure.Models.UserProfileModelLite;
import com.soundpaletteui.R;

import java.util.List;

public class UserSelectedAdapter extends RecyclerView.Adapter<com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter.SelectedUserViewHolder> {

    public interface OnRemoveUserClickListener {
        void onRemoveUser(UserProfileModelLite user);
    }

    private final List<UserProfileModelLite> selectedUsers;
    private final OnRemoveUserClickListener listener;

    public UserSelectedAdapter(List<UserProfileModelLite> selectedUsers, OnRemoveUserClickListener listener) {
        this.selectedUsers = selectedUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_user_selected, parent, false);
        return new SelectedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedUserViewHolder holder, int position) {
        UserProfileModelLite user = selectedUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

    class SelectedUserViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameText;
        private final ImageView removeIcon;

        public SelectedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            removeIcon = itemView.findViewById(R.id.removeIcon);
        }

        public void bind(final UserProfileModelLite user) {
            usernameText.setText(user.getUsername());
            removeIcon.setOnClickListener(v -> listener.onRemoveUser(user));
        }
    }
}
