package com.soundpaletteui.Activities.Trending;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Messages.ChatroomFragment;
import com.soundpaletteui.Activities.Posts.PostFragment;
import com.soundpaletteui.Activities.Profile.ProfileViewFragment;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserSearchModel;
import com.soundpaletteui.SPApiServices.ApiClients.ChatClient;
import com.soundpaletteui.SPApiServices.ApiClients.UserClient;
import com.soundpaletteui.Infrastructure.Models.Chat.ChatroomModelLite;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;
import com.soundpaletteui.SPApiServices.SPWebApiRepository;
import com.soundpaletteui.Infrastructure.Utilities.Navigation;
import com.soundpaletteui.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchTagsAdapter extends RecyclerView.Adapter<SearchTagsAdapter.TagsViewHolder> {

    private final List<TagModel> tags;
    private Context context;

    public SearchTagsAdapter(List<TagModel> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public TagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_search_post_tags, parent, false);
        return new TagsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagsViewHolder holder, int position) {
        TagModel tag = tags.get(position);
        final String tagName = tag.getTagName();

        holder.tagNameDisplay.setText(tagName);
        holder.tagContainer.setOnClickListener(v -> seePostsForTag(tag.getTagId()));
    }

    private void seePostsForTag(int tagId){
        ((MainScreenActivity) context).viewPostsByTags(String.valueOf(tagId));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagsViewHolder extends RecyclerView.ViewHolder {
        TextView tagNameDisplay;
        LinearLayout tagContainer;

        public TagsViewHolder(@NonNull View itemView) {
            super(itemView);
            tagNameDisplay = itemView.findViewById(R.id.tagName);
            tagContainer = itemView.findViewById(R.id.tag_container);

        }
    }
}
