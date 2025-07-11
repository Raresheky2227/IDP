package com.yourorg.auth.dto;

public class SignupRequest {
    private String username;
    private String password;
    private String roles; // e.g. "ROLE_USER"

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
}
