package com.example.t_star.REAL_TIME_DATABASE.Validation;

import android.util.Patterns;

/**
 * Simple centralized validation utility.
 * Add/modify rules here and the whole app gets consistent validation.
 */
public class User_Input_Validator {

    // Validate full name (minimum 2 chars, letters and spaces)
    public static boolean isValidFullName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        return trimmed.length() >= 2;
    }

    // Validate roll number (non-empty)
    public static boolean isValidRollNo(String roll) {
        if (roll == null) return false;
        return !roll.trim().isEmpty();
    }

    // Validate phone (10 digits numeric)
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String p = phone.trim();
        return p.matches("\\d{10}");
    }

    // Validate email using Android Patterns
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Utility: split full name into firstname & lastname
    public static String[] splitFullName(String fullName) {
        if (fullName == null) return new String[]{ "", "" };
        String trimmed = fullName.trim();
        if (trimmed.isEmpty()) return new String[]{ "", "" };
        String[] parts = trimmed.split("\\s+", 2);
        String first = parts[0];
        String last = parts.length > 1 ? parts[1] : "";
        return new String[]{ first, last };
    }
}
