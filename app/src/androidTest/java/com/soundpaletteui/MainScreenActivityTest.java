package com.soundpaletteui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.soundpaletteui.Activities.MainScreenActivity;
import com.soundpaletteui.Activities.Home.HomeFragment;
import com.soundpaletteui.Activities.Messages.MessageFragment;
import com.soundpaletteui.Activities.Posts.CreatePostFragment;
import com.soundpaletteui.Activities.Profile.ProfileFragment;
import com.soundpaletteui.Activities.Trending.SearchFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainScreenActivityTest {

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<MainScreenActivity> activityRule =
            new ActivityTestRule<>(MainScreenActivity.class); // Launch MainScreenActivity for each test


    // Test to ensure the default fragment is HomeFragment when MainScreenActivity is launched
    @Test
    public void testDefaultFragmentIsHome() {
        // Wait for HomeFragment to load
        onView(withId(R.id.mainScreenFrame)).check(matches(isDisplayed()));

        // Verify that the HomeFragment is initially displayed
        onView(withId(R.id.frame_explore)).check(matches(isDisplayed()));
    }

    // Test to ensure that the fragment switches when the Home button is clicked
    @Test
    public void testNavigationToHomeFragment() {
        // Click the Home button
        onView(withId(R.id.nav_home)).perform(click());

        // Wait for HomeFragment to load
        onView(withId(R.id.mainScreenFrame)).check(matches(isDisplayed()));

        // Verify that the HomeFragment is displayed
        onView(withId(R.id.frame_explore)).check(matches(isDisplayed()));
    }

    // Test to ensure that the fragment switches when the Profile button is clicked
    @Test
    public void testNavigationToProfileFragment() {
        // Click the Profile button
        onView(withId(R.id.nav_profile)).perform(click());

        // Wait for ProfileFragment to load
        onView(withId(R.id.mainScreenFrame)).check(matches(isDisplayed()));

        // Verify that the ProfileFragment is displayed
        onView(withId(R.id.profile_bio)).check(matches(isDisplayed()));
    }

    // Test to ensure that the fragment switches when the Create button is clicked
    @Test
    public void testNavigationToCreateFragment() {
        // Click the Create button
        onView(withId(R.id.nav_create)).perform(click());

        // Wait for CreatePostFragment to load
        onView(withId(R.id.mainScreenFrame)).check(matches(isDisplayed()));

        // Verify that the CreatePostFragment is displayed
        onView(withId(R.id.postFragment)).check(matches(isDisplayed()));
    }

    // Test to ensure that the fragment switches when the Messages button is clicked
    @Test
    public void testNavigationToMessagesFragment() {
        // Click the Messages button
        onView(withId(R.id.nav_msg)).perform(click());

        // Wait for MessageFragment to load
        onView(withId(R.id.mainScreenFrame)).check(matches(isDisplayed()));

        // Verify that the MessageFragment is displayed
        onView(withId(R.id.createChatroomButton)).check(matches(isDisplayed()));
    }

    // Test to ensure that the fragment switches when the Search button is clicked
    @Test
    public void testNavigationToSearchFragment() {
        // Click the Search button
        onView(withId(R.id.nav_search)).perform(click());

        // Wait for SearchFragment to load
        onView(withId(R.id.mainScreenFrame)).check(matches(isDisplayed()));

        // Verify that the SearchFragment is displayed
        onView(withId(R.id.postFragment)).check(matches(isDisplayed()));
    }

    // Unregister the IdlingResource after the test
    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
