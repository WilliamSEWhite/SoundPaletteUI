package com.soundpaletteui.Infrastructure.Utilities;

public class LoginValidator {

    // Validates the username input based on specific criteria
    public static String validateUsername(String username) {
        // Username cannot be empty
        if (username.isEmpty()) {
            return "Username cannot be empty";
        }
        // Username should be less than or equal to 20 characters
        if (username.length() > 20) {
            return "Username should be less than 20 characters";
        }
        return null; // Username is valid
    }

    // Validates the password input based on specific criteria
    public static String validatePassword(String password) {
        // Password cannot be empty
        if (password.isEmpty()) {
            return "Password cannot be empty";
        }
        // Password length must be between 8 and 20 characters
        if (password.length() < 8 || password.length() > 20) {
            return "Password must be 8-20 characters, contain numbers and a special character";
        }
        // Password must contain at least one number and one special character
        if (!password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()].*")) {
            return "Password must be 8-20 characters, contain numbers and a special character";
        }
        return null; // Password is valid
    }
}
