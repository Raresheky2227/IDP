package com.yourorg.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// This controller serves any authenticated-user endpoints
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public UserSummary getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return new UserSummary(user.getUsername(), user.getAuthorities());
    }

    // Simple DTO to return username + roles
    public static record UserSummary(String username, Object roles) {}
}
