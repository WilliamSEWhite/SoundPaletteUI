package com.soundpaletteui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.soundpaletteui.Activities.SplashActivity;
import com.soundpaletteui.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SplashActivityTest {

    @Rule
    public ActivityScenarioRule<SplashActivity> activityRule =
            new ActivityScenarioRule<>(SplashActivity.class);

    @Test
    public void splashScreen_ShowsEmojiBackground() {
        // Check if splash emoji background is visible at start
        onView(withId(R.id.splashEmojiBackground))
                .check(matches(isDisplayed()));
    }

    @Test
    public void splashScreen_NavigatesAfterDelay() throws InterruptedException {
        // Wait for splash screen delay (SPLASH_DISPLAY_LENGTH + little buffer)
        Thread.sleep(1500);

        // We cannot know exactly which activity opens without controlling saved login,
        // but normally, if no credentials => login screen.

        // Check if LoginActivity main layout is displayed (assumes LoginActivity shows something with id login_root)
        onView(withId(R.id.root_layout))
                .check(matches(isDisplayed()));
    }
}
