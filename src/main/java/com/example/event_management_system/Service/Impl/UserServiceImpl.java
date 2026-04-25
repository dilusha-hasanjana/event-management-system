package com.example.event_management_system.Service.Impl;

import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Factory.UserFactory;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Repo.EventRepository;
import com.example.event_management_system.Repo.UserRepository;
import com.example.event_management_system.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserDTO userDTO, Role role) {
        User user;
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        if (role == Role.ADMIN) {
            user = UserFactory.createAdmin(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    encodedPassword,
                    userDTO.getFullName()
            );
        } else {
            user = UserFactory.createUser(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    encodedPassword,
                    userDTO.getFullName()
            );
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserDTO userDTO) {
        User user = getUserById(id);

        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setActive(userDTO.isActive());
        
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public void changeUserRole(Long id, Role role) {
        User user = getUserById(id);
        user.setRole(role); // Update the user's role
        userRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive()); // Switch between true and false
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User registerForEvent(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        user.registerForEvent(event);
        return userRepository.save(user);
    }

    @Override
    public User unregisterFromEvent(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        user.unregisterFromEvent(event);
        return userRepository.save(user);
    }

    @Override
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    public long getAdminCount() {
        return userRepository.countByRole(Role.ADMIN);
    }

    @Override
    public long getRegularUserCount() {
        return userRepository.countByRole(Role.USER);
    }
}