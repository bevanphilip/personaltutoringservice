package com.example.personaltutoringservice;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginUtilsTest  {

    private LoginUtils loginUtils;

    @Before
    public void setUp() {
        loginUtils = new LoginUtils();
    }

    // Login: Test case 4
    @Test
    public void testEmptyFields() {

        boolean result = loginUtils.validateInputs("", "");

        assertFalse(result);
    }

    @Test
    public void testValidFields() {

        boolean result = loginUtils.validateInputs("test@email.com", "password123");

        assertTrue(result);
    }

    // Login: Test case 2
    @Test
    public void testIncorrectPassword() {

        String result = loginUtils.checkPassword("correctPass", "wrongPass");

        assertEquals("invalid_password", result);
    }

    @Test
    public void testCorrectPassword() {

        String result = loginUtils.checkPassword("correctPass", "correctPass");

        assertEquals("success", result);
    }
}