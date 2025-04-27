package com.soundpaletteui.Activities.SearchAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.R;

import java.util.List;

// Display of tag names the user has selected in a RecyclerView.
// Handles the user adding/removing a tag by tapping an icon.
public class TagSelectedAdapter extends RecyclerView.Adapter<TagSelectedAdapter.SelectedTagViewHolder> {
    private final List<TagModel> selectedTags;
    private final OnRemoveTagClickListener listener;

    public interface OnRemoveTagClickListener {
        void onRemoveTag(TagModel tag);
    }

    // Create the layout for each tag item in the list
    public TagSelectedAdapter(List<TagModel> selectedTags, OnRemoveTagClickListener listener) {
        this.selectedTags = selectedTags;
        this.listener = listener;
    }

    @NonNull
    @Override
    // Creates a new ViewHolder for a selected tag
    public SelectedTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_selected, parent, false);
        return new SelectedTagViewHolder(view);
    }

    @Override
    // Binds the tag information to the ViewHolder
    public void onBindViewHolder(@NonNull SelectedTagViewHolder holder, int position) {
        TagModel tag = selectedTags.get(position);
        holder.bind(tag);
    }

    @Override
    // Return how many tags are currently selected
    public int getItemCount() {
        return selectedTags.size();
    }

    // ViewHolder class that controls each selected tag item
    class SelectedTagViewHolder extends RecyclerView.ViewHolder {
        private final TextView searchResultItem;
        private final ImageView removeIcon;

        // ViewHolder class for displaying each selected tag
        public SelectedTagViewHolder(@NonNull View itemView) {
            super(itemView);
            searchResultItem = itemView.findViewById(R.id.searchResultItem);
            removeIcon = itemView.findViewById(R.id.removeIcon);
        }

        // Set up the tag name and remove action
        public void bind(final TagModel tag) {
            searchResultItem.setText(tag.getTagName());
            removeIcon.setOnClickListener(v -> listener.onRemoveTag(tag));
        }
    }
}
