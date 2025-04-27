package com.soundpaletteui.Activities.SearchAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.R;

import java.util.List;

// Handles showing a list of users the person has selected in a RecyclerView.
// Displays the usernames and lets the user remove a name by tapping the remove icon.
public class UserSelectedAdapter extends RecyclerView.Adapter<com.soundpaletteui.Activities.SearchAdapters.UserSelectedAdapter.SelectedUserViewHolder> {
    private final List<String> selectedUsers;
    private final OnRemoveUserClickListener listener;

    // Listener interface for when a user wants to remove a tag
    public interface OnRemoveUserClickListener {
        void onRemoveUser(String user);
    }

    // Creates the adapter with the selected users and the remove action
    public UserSelectedAdapter(List<String> selectedUsers, OnRemoveUserClickListener listener) {
        this.selectedUsers = selectedUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    // Creates a new ViewHolder for a selected tag
    public SelectedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_selected, parent, false);
        return new SelectedUserViewHolder(view);
    }

    @Override
    // Binds the tag information to the ViewHolder
    public void onBindViewHolder(@NonNull SelectedUserViewHolder holder, int position) {
        String user = selectedUsers.get(position);
        holder.bind(user);
    }

    @Override
    // Returns the number of selected tags
    public int getItemCount() {
        return selectedUsers.size();
    }

    // ViewHolder class that controls each selected tag item
    class SelectedUserViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameText;
        private final ImageView removeIcon;

        // ViewHolder class for displaying each selected tag
        public SelectedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.searchResultItem);
            removeIcon = itemView.findViewById(R.id.removeIcon);
        }

        // Sets the tag text and handles remove icon clicks
        public void bind(final String user) {
            usernameText.setText(user);
            removeIcon.setOnClickListener(v -> listener.onRemoveUser(user));
        }
    }
}
