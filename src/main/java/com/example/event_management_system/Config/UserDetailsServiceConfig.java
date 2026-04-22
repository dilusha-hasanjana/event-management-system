package com.example.event_management_system.Config;

import com.example.event_management_system.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            try {
                return userService.getUserByUsername(username);
            } catch (Exception e) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
        };
    }
}