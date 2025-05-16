package com.yourorg.auth.model;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User entity for authentication and authorization, implementing Spring Security's UserDetails.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Comma-separated roles, e.g., "ROLE_USER,ROLE_VIP"
     */
    @Column(nullable = false)
    private String roles;

    public User() {}

    public User(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    /**
     * Returns roles as a comma-separated String (for use in custom logic, not by Spring Security).
     */
    public String getRoles() {
        return roles;
    }

    /**
     * Returns roles as a list, e.g., ["ROLE_USER", "ROLE_VIP"]
     */
    public List<String> getRolesList() {
        if (roles == null || roles.isBlank()) return List.of();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public void setUsername(String u) { this.username = u; }
    public void setPassword(String p) { this.password = p; }
    public void setRoles(String r)   { this.roles = r; }

    // UserDetails interface methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isBlank()) return Collections.emptyList();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked()  { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // Getters and setters for id (if needed elsewhere)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
