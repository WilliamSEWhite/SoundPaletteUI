package com.soundpaletteui.Infrastructure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TagSelectAdapter extends RecyclerView.Adapter {

    private List<TagModel> tagList;
    private Context context;

    public TagSelectAdapter(ArrayList<TagModel> tagList, Context context) {
        this.tagList = tagList;
        this.context = context;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tagName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tagName);
            checkBox = itemView.findViewById(R.id.tagSelected);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tag_select,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // allow access to tagName and checkBox
        ViewHolder viewHolder = (ViewHolder)holder;

        TagModel tag = tagList.get(position);
        viewHolder.tagName.setText(tag.getTagName());
        viewHolder.checkBox.setChecked(tag.isSelected());

        // toggle checkbox
        viewHolder.itemView.setOnClickListener(v -> {
            boolean newState = !viewHolder.checkBox.isChecked();
            viewHolder.checkBox.setChecked(newState);
            tag.setSelected(newState);
        });
        // update TagModel when checkbox is clicked
        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                tag.setSelected(isChecked));
    }

    /*public void onBindViewHolder(ViewHolder holder, int position) {
        TagModel tag = tagList.get(position);
        holder.tagName.setText(tag.getTagName());
        holder.checkBox.setChecked(tag.isSelected());
    }*/

    /** gets the size of the tag list */
    public int getItemCount() {
        return tagList != null ? tagList.size() : 0;
    }

    /** returns the selected tags */
    public ArrayList<TagModel> getSelectedTags() {
        ArrayList<TagModel> selected = new ArrayList<>();
        for(TagModel tag : tagList) {
            if(tag.isSelected()) {
                selected.add(tag);
            }
        }
        return selected;
    }

}
