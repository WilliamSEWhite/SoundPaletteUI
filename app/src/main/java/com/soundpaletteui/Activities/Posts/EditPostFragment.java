package com.soundpaletteui.Activities.Posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class EditPostFragment  extends Fragment {
    private static final int POST_ID = 1;

    public static EditPostFragment newInstance(int postId) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle args = new Bundle();
        args.putInt("POST_ID", postId);
        fragment.setArguments(args);
        return fragment;
    }

}
