package com.example.medsynex.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void validateNames() {
        //ECP
        assert(userDetailsService.validateNames("Radu")); // Valid names that match the pattern
        assert(!userDetailsService.validateNames("radu")); // No uppercase letter (invalid)
        assert(!userDetailsService.validateNames("Radu3")); // Characters other than letters (invalid)
        assert(!userDetailsService.validateNames("")); // Empty string (invalid)
        assert(!userDetailsService.validateNames(null)); // Null (invalid)

        //BVA
        assert(!userDetailsService.validateNames("I")); // Just one uppercase letter, no lowercase following (invalid)
        assert(userDetailsService.validateNames("Io")); // One uppercase followed by one lowercase, minimal valid case
        assert(userDetailsService.validateNames("Ion")); // One uppercase followed by two lowercase (valid)
    }

    @Test
    void validatePassword() {
        //ECP
        assert(userDetailsService.validatePassword("Valid123$")); // Valid passwords that meet all criteria
        assert(!userDetailsService.validatePassword("Va1%")); // Shorter than 8 characters (invalid)
        assert(!userDetailsService.validatePassword("INVALID123$")); // Missing lowercase letter (invalid)
        assert(!userDetailsService.validatePassword("invalid123$")); // Missing uppercase letter (invalid)
        assert(!userDetailsService.validatePassword("Invalid$abc")); // Missing at least one digit (invalid)
        assert(!userDetailsService.validatePassword("Invalid123")); // Missing special character (invalid)
        assert(!userDetailsService.validatePassword("")); // Empty string (invalid)
        assert(!userDetailsService.validatePassword(null)); // Null (invalid)

        //BVA
        assert(!userDetailsService.validatePassword("Val123$")); // 7 characters, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("Val123$D")); // 8 characters, the minimum valid length (valid)
        assert(userDetailsService.validatePassword("Val123$D!")); // 9 characters, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("ABIDE123!")); // No lowercase letter, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("AbCDE123!")); // 1 lowercase letter, minimal valid case
        assert(userDetailsService.validatePassword("AbcDE123!")); // 2 lowercase letters, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("abide123!")); // No uppercase letter, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("aBide123!")); // 1 uppercase letter, minimal valid case
        assert(userDetailsService.validatePassword("aBIde123!")); // 2 uppercase letters, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("Bonus$%&")); // No digit, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("Bonus$%&1")); // 1 digit, minimal valid case
        assert(userDetailsService.validatePassword("Bonus$%&12")); // 2 digits, just above the minimum (valid)

        assert(!userDetailsService.validatePassword("Bonus1234")); // No special character, just below the minimum (invalid)
        assert(userDetailsService.validatePassword("Bonus1234!")); // 1 special character, minimal valid case
        assert(userDetailsService.validatePassword("Bonus1234!@")); // 2 special characters, just above the minimum (valid)
    }
}
