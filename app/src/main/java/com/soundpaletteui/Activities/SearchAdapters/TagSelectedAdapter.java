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

public class TagSelectedAdapter extends RecyclerView.Adapter<TagSelectedAdapter.SelectedTagViewHolder> {

    public interface OnRemoveTagClickListener {
        void onRemoveTag(TagModel tag);
    }

    private final List<TagModel> selectedTags;
    private final OnRemoveTagClickListener listener;

    public TagSelectedAdapter(List<TagModel> selectedTags, OnRemoveTagClickListener listener) {
        this.selectedTags = selectedTags;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectedTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_selected, parent, false);
        return new SelectedTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedTagViewHolder holder, int position) {
        TagModel tag = selectedTags.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return selectedTags.size();
    }

    class SelectedTagViewHolder extends RecyclerView.ViewHolder {
        private final TextView searchResultItem;
        private final ImageView removeIcon;

        public SelectedTagViewHolder(@NonNull View itemView) {
            super(itemView);
            searchResultItem = itemView.findViewById(R.id.searchResultItem);
            removeIcon = itemView.findViewById(R.id.removeIcon);
        }

        public void bind(final TagModel tag) {
            searchResultItem.setText(tag.getTagName());
            removeIcon.setOnClickListener(v -> listener.onRemoveTag(tag));
        }
    }
}
