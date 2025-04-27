package com.soundpaletteui.Activities.SearchAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.R;

import java.util.List;

// Adapter for displaying a list of tags inside a search result.
// Allows users to tap a tag to select it.
public class TagSearchAdapter extends RecyclerView.Adapter<TagSearchAdapter.TagViewHolder> {
    private final List<TagModel> tags;
    private final OnTagClickListener listener;

    // Listener interface for when a tag is clicked
    public interface OnTagClickListener {
        void onTagClick(TagModel tag);
    }

    // Creates the adapter with the list of users and the click action
    public TagSearchAdapter(List<TagModel> tags, OnTagClickListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    @NonNull
    @Override
    // Creates a new ViewHolder for a tag search result
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_result, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    // Creates a new ViewHolder for a tag search result
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagModel tag = tags.get(position);
        holder.bind(tag);
    }

    @Override
    // Returns the number of tags
    public int getItemCount() {
        return tags.size();
    }

    // ViewHolder class for displaying each tag
    class TagViewHolder extends RecyclerView.ViewHolder {
        private final TextView searchResultItem;

        // Link the TextView from the layout
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            searchResultItem = itemView.findViewById(R.id.searchResultItem);
        }

        // Sets the text for the tag and handles click behaviour
        public void bind(final TagModel tag) {
            searchResultItem.setText(tag.getTagName());
            itemView.setOnClickListener(v -> listener.onTagClick(tag));
        }
    }
}
