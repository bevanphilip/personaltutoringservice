package com.example.personaltutoringservice;

public class LoginUtils {

    public boolean validateInputs(String email, String password) {
        return email != null && !email.trim().isEmpty()
                && password != null && !password.trim().isEmpty();
    }

    public String checkPassword(String correctPassword, String enteredPassword) {
        if (correctPassword == null) return "user_not_found";
        if (!correctPassword.equals(enteredPassword)) return "invalid_password";
        return "success";
    }
}