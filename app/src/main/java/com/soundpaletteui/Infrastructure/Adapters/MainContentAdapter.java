package com.soundpaletteui.Infrastructure.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.User.UserModel;
import com.soundpaletteui.R;

import java.util.List;

// Adapter for displaying a list of users in a RecyclerView, showing their usernames and passwords (for testing purposes).
public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.ViewHolder> {
    private List<UserModel> userList;

    public MainContentAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    // Creates a new ViewHolder for a user item
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_home_test,parent,false);
        return new ViewHolder(view);
    }

    @Override
    // Binds the user information to the ViewHolder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.password.setText(user.getPassword());
    }

    @Override
    // Returns the number of users in the list
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class to hold the view for each user
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView password;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            password = (TextView) itemView.findViewById(R.id.password);
        }
    }
}
