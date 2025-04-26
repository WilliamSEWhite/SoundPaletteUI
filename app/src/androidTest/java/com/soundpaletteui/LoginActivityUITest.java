package com.soundpaletteui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.intent.Intents;

import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import com.soundpaletteui.Activities.MainScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {

    // Launch LoginActivity manually for each test
    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false); // Manually control when to launch the activity

    // Test for empty username and password
    @Test
    public void testLoginEmptyUsernamePassword_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Username and Password cannot be empty"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for empty username
    @Test
    public void testLoginEmptyUsername_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("validPassword123!"), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Username and Password cannot be empty"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for empty password
    @Test
    public void testLoginEmptyPassword_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText("validUser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Username and Password cannot be empty"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for invalid login with incorrect credentials
    @Test
    public void testLoginInvalidCredentials_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText("invalidUser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("wrongPassword!"), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Invalid username or password"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for password that is too short
    @Test
    public void testLoginPasswordTooShort_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText("validUser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("short"), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Password must be at least 8 characters long"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for password without a special character
    @Test
    public void testLoginPasswordWithoutSpecialCharacter_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText("validUser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("Password123"), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Password must contain at least one special character"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for password without numbers
    @Test
    public void testLoginPasswordWithoutNumber_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText("validUser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("Password!"), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Password must contain at least one number"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for password with only letters and no special characters or numbers
    @Test
    public void testLoginPasswordOnlyLetters_ShowsToast() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.username)).perform(typeText("validUser"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("Password"), closeSoftKeyboard());
        onView(withId(R.id.frame_login)).perform(click());

        onView(withText("Password must contain at least one special character and one number"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // Test for successful login and navigation to MainScreenActivity
    @Test
    public void testLogin_SuccessNavigatesToMainScreen() {
        // Start monitoring intents
        init();

        activityRule.launchActivity(new Intent());

        // Simulate user entering valid login credentials
        onView(withId(R.id.username)).perform(typeText("user1"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password1!"), closeSoftKeyboard());

        // Simulate clicking the login button
        onView(withId(R.id.frame_login)).perform(click());

        // Check if MainScreenActivity is launched
        intended(hasComponent(MainScreenActivity.class.getName()));

        // Stop monitoring intents
        release();
    }
}
