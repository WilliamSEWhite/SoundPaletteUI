package com.soundpaletteui.Infrastructure.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

// Manages the user's dark mode preference settings
public class DarkModePreferences {
    private static final String PREFS_NAME = "dark_mode_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";

    // Checks if dark mode is enabled
    public static boolean isDarkModeEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_DARK_MODE, false); // Default is false (light mode)
    }

    // Sets the dark mode preference
    public static void setDarkModeEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, enabled).apply();
    }
}
