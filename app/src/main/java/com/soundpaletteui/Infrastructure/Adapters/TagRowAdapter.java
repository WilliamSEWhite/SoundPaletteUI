package com.soundpaletteui.Infrastructure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.R;

import java.util.ArrayList;

public class TagRowAdapter extends ArrayAdapter<TagModel> {

    ArrayList<TagModel> selectedTags = new ArrayList<>();

    public TagRowAdapter(@NonNull Context context, ArrayList<TagModel> teamList) {
        super(context, 0, teamList);
    }//end constructor

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // get view
        View currentItemView = convertView;

        // if the view is null, inflate a new one of player row
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_tag_row, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        TagModel currentTag = getItem(position);

        // Assign value retrieved from player model to text view in player row
        TextView tagName = currentItemView.findViewById(R.id.tag_name);
        tagName.setText(String.valueOf(currentTag.getTagName()));

        // Assign value retrieved from player model to text view in player row
//        CheckBox teamIsActive = currentItemView.findViewById(R.id.teamIsActive);
//        teamIsActive.setChecked(currentTeam.getIsActive());

//        teamIsActive.setOnClickListener(v -> teamIsActiveToggled(currentTeam, teamIsActive.isChecked()));

        // then return the recyclable view
        return currentItemView;
    }
    private void updateTagIsSelected(TagModel tag){

    }
}
