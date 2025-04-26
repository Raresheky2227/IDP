package com.raresheky.userauth.controller;

import com.raresheky.userauth.dto.*;
import com.raresheky.userauth.entity.User;
import com.raresheky.userauth.repository.UserRepository;
import com.raresheky.userauth.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepo,
                          PasswordEncoder encoder) {
        this.authManager = authManager;
        this.jwtUtil     = jwtUtil;
        this.userRepo    = userRepo;
        this.encoder     = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto dto) {
        if (userRepo.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        User u = new User(
                dto.getUsername(),
                encoder.encode(dto.getPassword()),
                List.of("ROLE_USER")
        );
        userRepo.save(u);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        UserDetails ud = userRepo.findByUsername(dto.getUsername())
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r))
                                .toList()
                ))
                .orElseThrow();
        String token = jwtUtil.generateToken(ud);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
