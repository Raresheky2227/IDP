package com.yourorg.auth.controller;

import com.yourorg.auth.model.User;
import com.yourorg.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public UserSummary getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return new UserSummary(user.getUsername(), user.getAuthorities());
    }

    // NEW: Lookup roles by username (for cross-service queries)
    @GetMapping("/{username}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Assuming roles are stored as "ROLE_USER,ROLE_VIP"
        String rolesStr = user.getRoles();
        List<String> roles = Arrays.asList(rolesStr.split("\\s*,\\s*"));
        return ResponseEntity.ok(roles);
    }

    // Simple DTO to return username + roles
    public static record UserSummary(String username, Object roles) {}
}
