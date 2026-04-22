package com.example.event_management_system.Dto;

import com.example.event_management_system.Model.Role;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private Role role;
    private boolean active;
    private int registeredEventCount;

    public UserDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getRegisteredEventCount() { return registeredEventCount; }
    public void setRegisteredEventCount(int registeredEventCount) { this.registeredEventCount = registeredEventCount; }
}