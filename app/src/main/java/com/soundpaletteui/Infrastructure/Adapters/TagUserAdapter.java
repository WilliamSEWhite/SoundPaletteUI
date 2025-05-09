package com.soundpaletteui.Infrastructure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.R;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;

import java.util.List;

// Adapter for displaying a list of tagged users. Allows the user to click and view the profile of a tagged user.
public class TagUserAdapter extends RecyclerView.Adapter<TagUserAdapter.ViewHolder> {
    private List<String> tagList;
    private FragmentManager fragmentManager;
    private Context context;

    public TagUserAdapter(List<String> tagList, Context context) {
        this.tagList = tagList;
        this.context = context;
    }

    // ViewHolder class to hold each tagged user's view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.userTagName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_tags_user, parent, false);
        return new ViewHolder(view);
    }

    // Bind the username to the ViewHolder and set up click behaviour
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = tagList.get(position);
        holder.tagName.setText("@" + username);

        // Open the selected user's profile when their tag is clicked
        holder.itemView.setOnClickListener(v -> {
            ProfileViewFragment profileFragment = ProfileViewFragment.newInstance(username);
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            Navigation.replaceFragment(fragmentManager, profileFragment, "PROFILE_VIEW_FRAGMENT_" + username, R.id.mainScreenFrame);
        });
    }

    @Override
    // Return the number of tagged users
    public int getItemCount() {
        return tagList != null ? tagList.size() : 0;
    }
}
