package com.example.event_management_system.Service;

import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Model.Role;

import java.util.List;

public interface UserService {
    User createUser(UserDTO userDTO, Role role);
    User updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    List<User> getUsersByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User registerForEvent(Long userId, Long eventId);
    User unregisterFromEvent(Long userId, Long eventId);
    long getTotalUserCount();
    long getAdminCount();
    long getRegularUserCount();
}