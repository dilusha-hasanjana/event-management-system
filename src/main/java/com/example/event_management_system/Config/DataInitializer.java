package com.example.event_management_system.Config;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Service.EventService;
import com.example.event_management_system.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public DataInitializer(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public void run(String... args) {
        if (!userService.existsByUsername("admin")) {
            UserDTO adminDTO = new UserDTO();
            adminDTO.setUsername("admin");
            adminDTO.setEmail("admin@faculty.edu");
            adminDTO.setPassword("admin123");
            adminDTO.setFullName("System Administrator");

            User admin = userService.createUser(adminDTO, Role.ADMIN);
            System.out.println("Admin user created: admin / admin123");
        }

        if (!userService.existsByUsername("user")) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("user");
            userDTO.setEmail("user@faculty.edu");
            userDTO.setPassword("user123");
            userDTO.setFullName("John Doe");

            User user = userService.createUser(userDTO, Role.USER);
            System.out.println("Sample user created: user / user123");
        }

        if (eventService.getTotalEventCount() == 0) {
            User admin = userService.getUserByUsername("admin");

            EventDTO event1 = new EventDTO();
            event1.setTitle("Faculty Research Symposium");
            event1.setDescription("Annual research symposium showcasing faculty research projects across all departments.");
            event1.setLocation("Main Auditorium");
            event1.setEventDate(LocalDateTime.now().plusDays(7));
            event1.setPremium(true);
            event1.setFeatured(true);
            eventService.createEvent(event1, admin);

            EventDTO event2 = new EventDTO();
            event2.setTitle("Department Meeting");
            event2.setDescription("Monthly department meeting to discuss curriculum updates and faculty matters.");
            event2.setLocation("Conference Room A");
            event2.setEventDate(LocalDateTime.now().plusDays(3));
            event2.setPremium(false);
            event2.setFeatured(false);
            eventService.createEvent(event2, admin);

            EventDTO event3 = new EventDTO();
            event3.setTitle("Guest Lecture: AI in Education");
            event3.setDescription("Special guest lecture on the impact of Artificial Intelligence in modern education.");
            event3.setLocation("Lecture Hall B");
            event3.setEventDate(LocalDateTime.now().plusDays(14));
            event3.setPremium(true);
            event3.setFeatured(false);
            eventService.createEvent(event3, admin);

            System.out.println("Sample events created successfully!");
        }
    }
}