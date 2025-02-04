package com.soundpaletteui.Activities.Messages;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.soundpaletteui.R;

public class MessageFragment extends Fragment {

    private TextView messagesTextView;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        // Initialize the TextView
        messagesTextView = rootView.findViewById(R.id.messages_textview);

        // Retrieve the message from the Bundle
        String message = getArguments() != null ? getArguments().getString("message") : "Default Message";

        // Update the text
        updateText(message);

        return rootView;
    }

    // Method to update the text
    public void updateText(String newText) {
        if (messagesTextView != null) {
            messagesTextView.setText(newText);
        }
    }
    // Method to update the text
    public void updateTextSize(int newTextSize) {
        if (messagesTextView != null) {
            messagesTextView.setTextSize(newTextSize);
        }
    }

    // Factory method to create a new instance of MessageFragment with a message
    public static MessageFragment newInstance(String message) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }
}
