package com.yourorg.auth.service;

import com.yourorg.auth.dto.SignupRequest;
import com.yourorg.auth.model.User;
import com.yourorg.auth.repository.UserRepository;
import com.yourorg.auth.security.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.amqp.core.AmqpTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class UserServiceTests {

    @Autowired
    private UserRepository repo;

    private UserService svc;
    private PasswordEncoder encoder = new BCryptPasswordEncoder();
    private JwtUtil jwtUtil = new JwtUtil();
    private AmqpTemplate amqpTemplate = mock(AmqpTemplate.class); // <-- add mock

    @BeforeEach
    void init() {
        svc = new UserService(repo, encoder, jwtUtil, amqpTemplate); // <-- pass mock here
    }

    @Test
    void registerAndLoadUser() {
        SignupRequest r = new SignupRequest();
        r.setUsername("bob");
        r.setPassword("pwd");
        r.setRoles("ROLE_USER");
        svc.registerUser(r);

        User u = (User) svc.loadUserByUsername("bob");
        assertEquals("bob", u.getUsername());
        assertTrue(encoder.matches("pwd", u.getPassword()));
    }
}
