package com.soundpaletteui.Activities.SearchAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
                .inflate(R.layout.adapter_search_selected, parent, false);
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
        private final TextView searchResultItem;
        private final ImageView removeIcon;

        public SelectedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            searchResultItem = itemView.findViewById(R.id.searchResultItem);
            removeIcon = itemView.findViewById(R.id.removeIcon);
        }

        public void bind(final UserProfileModelLite user) {
            searchResultItem.setText(user.getUsername());
            removeIcon.setOnClickListener(v -> listener.onRemoveUser(user));
        }
    }
}
