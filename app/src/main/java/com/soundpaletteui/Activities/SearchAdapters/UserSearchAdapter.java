package com.soundpaletteui.Activities.SearchAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.R;

import java.util.List;

// Handles showing a list of users in a RecyclerView when searching.
// Displays usernames and lets the user tap on a name to select it.
public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {
    private final List<String> users;
    private final OnUserClickListener listener;

    // Interface for setting up a click action when the user taps a name
    public interface OnUserClickListener {
        void onUserClick(String user);
    }

    // Creates the adapter with the list of users and the click action
    public UserSearchAdapter(List<String> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    // Creates a new ViewHolder for a tag search result
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_result, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    // Creates a new ViewHolder for a tag search result
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String user = users.get(position);
        holder.bind(user);
    }

    @Override
    // Return how many users are in the list
    public int getItemCount() {
        return users.size();
    }

    // ViewHolder class that controls how each user item works
    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameText;

        // Link the TextView from the layout
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.searchResultItem);
        }

        // Sets the text for the tag and handles click behaviour
        public void bind(final String user) {
            usernameText.setText(user);
            itemView.setOnClickListener(v -> listener.onUserClick(user));
        }
    }
}
