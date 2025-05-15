package com.yourorg.auth.controller;

import com.yourorg.auth.dto.JwtResponse;
import com.yourorg.auth.dto.LoginRequest;
import com.yourorg.auth.dto.SignupRequest;
import com.yourorg.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest req) {
        userService.registerUser(req);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.authenticateUser(req));
    }
}
