package com.soundpaletteui;

import static org.junit.Assert.*;

import com.soundpaletteui.Infrastructure.Utilities.LoginValidator;

import org.junit.Test;

public class LoginLogicTest {

    // Username Tests

    @Test
    public void testUsernameValidation_Empty() {
        String result = LoginValidator.validateUsername("");
        assertEquals("Username cannot be empty", result);
    }

    @Test
    public void testUsernameValidation_TooLong() {
        String result = LoginValidator.validateUsername("thisisaveryveryverylongusername");
        assertEquals("Username should be less than 20 characters", result);
    }

    @Test
    public void testUsernameValidation_Valid() {
        String result = LoginValidator.validateUsername("validUser");
        assertNull(result);
    }

    // Password Tests

    @Test
    public void testPasswordValidation_Empty() {
        String result = LoginValidator.validatePassword("");
        assertEquals("Password cannot be empty", result);
    }

    @Test
    public void testPasswordValidation_Weak() {
        String result = LoginValidator.validatePassword("weakpass");
        assertEquals("Password must be 8-20 characters, contain numbers and a special character", result);
    }

    @Test
    public void testPasswordValidation_TooShort() {
        String result = LoginValidator.validatePassword("S1!");
        assertEquals("Password must be 8-20 characters, contain numbers and a special character", result);
    }

    @Test
    public void testPasswordValidation_TooLong() {
        String result = LoginValidator.validatePassword("thisisaveryveryverylongpassword1!");
        assertEquals("Password must be 8-20 characters, contain numbers and a special character", result);
    }

    @Test
    public void testPasswordValidation_Valid() {
        String result = LoginValidator.validatePassword("StrongPass1!");
        assertNull(result);
    }

    @Test
    public void testPasswordValidation_NoSpecialChar() {
        String result = LoginValidator.validatePassword("Password123");
        assertEquals("Password must be 8-20 characters, contain numbers and a special character", result);
    }

    @Test
    public void testPasswordValidation_NoNumber() {
        String result = LoginValidator.validatePassword("Password!@#");
        assertEquals("Password must be 8-20 characters, contain numbers and a special character", result);
    }
}
