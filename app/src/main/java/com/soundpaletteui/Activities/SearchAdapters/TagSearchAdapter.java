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

public class TagSearchAdapter extends RecyclerView.Adapter<TagSearchAdapter.TagViewHolder> {

    public interface OnTagClickListener {
        void onTagClick(TagModel tag);
    }

    private final List<TagModel> tags;
    private final OnTagClickListener listener;

    public TagSearchAdapter(List<TagModel> tags, OnTagClickListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_search_result, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagModel tag = tags.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        private final TextView searchResultItem;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            searchResultItem = itemView.findViewById(R.id.searchResultItem);
        }

        public void bind(final TagModel tag) {
            searchResultItem.setText(tag.getTagName());
            itemView.setOnClickListener(v -> listener.onTagClick(tag));
        }
    }
}
