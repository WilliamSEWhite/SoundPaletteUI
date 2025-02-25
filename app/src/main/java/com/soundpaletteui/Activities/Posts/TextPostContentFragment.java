package com.soundpaletteui.Activities.Posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.R;

public class TextPostContentFragment extends Fragment {


    public TextPostContentFragment() {
        // Required empty public constructor
    }

    public static TextPostContentFragment newInstance() {
        return new TextPostContentFragment();
    }

    public String getContent(){
        View v = getView();

        String content = ((EditText)v.findViewById(R.id.text_content)).getText().toString();

        return content;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_post_content, container, false);
    }
}