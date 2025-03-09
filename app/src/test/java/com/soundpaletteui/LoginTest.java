package com.soundpaletteui;

import com.soundpaletteui.Activities.LoginRegister.LoginActivity;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        // Create an instance of LoginActivity before each test
        loginActivity = new LoginActivity();
    }

    // Test case for validating username
    @Test
    public void testUsernameValidation() {
        // Valid username
        assertTrue("Username should be valid", loginActivity.isValidUsername("validUsername"));
        // Invalid username (too long)
        assertFalse("Username should not exceed 20 characters", loginActivity.isValidUsername("this is a very long username"));
        // Invalid username (empty)
        assertFalse("Username should not be empty", loginActivity.isValidUsername(""));
    }

    // Test case for validating password
    @Test
    public void testPasswordValidation() {
        // Valid password
        assertTrue("Password should be valid", loginActivity.isValidPassword("Valid123!"));
        // Invalid password (empty)
        assertFalse("Password should not be empty", loginActivity.isValidPassword(""));
        // Invalid password (no special character)
        assertFalse("Password should have at least one special character", loginActivity.isValidPassword("Password123"));
        // Invalid password (no number)
        assertFalse("Password should have at least one number", loginActivity.isValidPassword("Password!"));
        // Invalid password (too short)
        assertFalse("Password should be between 6-20 characters", loginActivity.isValidPassword("short1!"));
    }
}