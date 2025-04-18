package com.soundpaletteui.Infrastructure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.R;

import java.util.ArrayList;
import java.util.List;

public class TagBasicAdapter extends RecyclerView.Adapter {

    private List<TagModel> tagList;
    private Context context;

    public TagBasicAdapter(ArrayList<TagModel> tagList, Context context) {
        this.tagList = tagList;
        this.context = context;
    }
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tags_basic,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // allow access to tagName
        ViewHolder viewHolder = (ViewHolder)holder;

        TagModel tag = tagList.get(position);
        viewHolder.tagName.setText(tag.getTagName());


    }

    /** gets the size of the tag list */
    public int getItemCount() {
        return tagList != null ? tagList.size() : 0;
    }

    /** returns the selected tags */
    /*public ArrayList<TagModel> getSelectedTags() {
        ArrayList<TagModel> selected = new ArrayList<>();
        for(TagModel tag : selected) {
            selected.add(tag);
        }
        return selected;
    }*/

}

