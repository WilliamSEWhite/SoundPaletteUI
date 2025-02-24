package com.soundpaletteui.Activities.Messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.soundpaletteui.R;
import com.soundpaletteui.UISettings;

/**
 * Displays messages or text-based content within a fragment.
 */
public class MessageFragment extends Fragment {

    private TextView messagesTextView;

    /**
     * Default constructor for MessageFragment.
     */
    public MessageFragment() {
    }

    /**
     * Returns a new instance of MessageFragment with a provided message.
     */
    public static MessageFragment newInstance(String message) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout and applies a gradient background.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        UISettings.applyBrightnessGradientBackground(rootView, 240f);
        messagesTextView = rootView.findViewById(R.id.messages_textview);
        String message = (getArguments() != null)
                ? getArguments().getString("message")
                : "Default Message";
        updateText(message);
        return rootView;
    }

    /**
     * Updates the displayed text in this fragment.
     */
    public void updateText(String newText) {
        if (messagesTextView != null) {
            messagesTextView.setText(newText);
        }
    }

    /**
     * Updates the text size in this fragment.
     */
    public void updateTextSize(int newTextSize) {
        if (messagesTextView != null) {
            messagesTextView.setTextSize(newTextSize);
        }
    }
}
