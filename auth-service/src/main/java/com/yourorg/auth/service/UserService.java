package com.yourorg.auth.service;

import com.yourorg.auth.dto.JwtResponse;
import com.yourorg.auth.dto.LoginRequest;
import com.yourorg.auth.dto.SignupRequest;
import com.yourorg.auth.model.User;
import com.yourorg.auth.repository.UserRepository;
import com.yourorg.auth.security.JwtUtil;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository repo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public void registerUser(SignupRequest req) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setRoles(req.getRoles());
        repo.save(u);
    }

    public JwtResponse authenticateUser(LoginRequest req) {
        UserDetails u = loadUserByUsername(req.getUsername());
        if (!encoder.matches(req.getPassword(), u.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(req.getUsername());
        return new JwtResponse(token, "Bearer", req.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
