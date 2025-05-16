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
import org.springframework.amqp.core.AmqpTemplate;
import org.json.JSONObject;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AmqpTemplate amqpTemplate;

    public UserService(UserRepository repo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       AmqpTemplate amqpTemplate) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.amqpTemplate = amqpTemplate;
    }

    public void registerUser(SignupRequest req) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setRoles(req.getRoles());
        repo.save(u);

        // Send notification event to RabbitMQ
        JSONObject event = new JSONObject();
        event.put("type", "signup");
        event.put("recipient", req.getUsername());
        amqpTemplate.convertAndSend("notification-queue", event.toString());
    }

    public JwtResponse authenticateUser(LoginRequest req) {
        User user = (User) loadUserByUsername(req.getUsername());
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        // If roles is a List or Set, pick the first, or join as comma-separated if needed
        String role = user.getRoles();
        if (role == null) {
            role = "ROLE_USER";
        }
        String token = jwtUtil.generateToken(req.getUsername(), role);
        return new JwtResponse(token, "Bearer", req.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
