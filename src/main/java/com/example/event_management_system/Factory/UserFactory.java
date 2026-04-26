package com.example.event_management_system.Factory;

import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;


public class UserFactory {

    // Create an Admin user
    public static User createAdmin(String username, String email, String password, String fullName) {
        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setFullName(fullName);
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        return admin;
    }

    // Create a regular User (student)
    public static User createUser(String username, String email, String password, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setRole(Role.USER);
        user.setActive(true);
        return user;
    }

}