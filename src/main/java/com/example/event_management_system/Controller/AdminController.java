package com.example.event_management_system.Controller;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.EventStatus;
import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Service.EventService;
import com.example.event_management_system.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("totalEvents", eventService.getTotalEventCount());
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("adminCount", userService.getAdminCount());
        model.addAttribute("regularUserCount", userService.getRegularUserCount());
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents().size());
        return "admin/dashboard";
    }

    @GetMapping("/events/get")
    public String showAddEventForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("event", new EventDTO());
        return "admin/add-event";
    }

    @PostMapping("/events/add")
    public String addEvent(@Valid @ModelAttribute("event") EventDTO eventDTO,
                           BindingResult result,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "admin/add-event";
        }

        try {
            // Combine date and time into eventDate
            if (eventDTO.getDate() != null && eventDTO.getTime() != null) {
                eventDTO.setEventDate(java.time.LocalDateTime.of(eventDTO.getDate(), eventDTO.getTime()));
            }
            
            eventService.createEvent(eventDTO, user);
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            return "redirect:/admin/events/get";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "admin/add-event";
        }
    }

    @GetMapping("/events")
    public String listEvents(@AuthenticationPrincipal User user, Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        return "admin/manage-events";
    }

    @GetMapping("/events/edit/{id}")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        EventDTO eventDTO = convertToDTO(event);
        model.addAttribute("event", eventDTO);
        model.addAttribute("eventId", id);
        return "admin/edit-event";
    }

    @PostMapping("/events/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("event") EventDTO eventDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("eventId", id);
            return "admin/edit-event";
        }

        try {
            // Combine date and time into eventDate
            if (eventDTO.getDate() != null && eventDTO.getTime() != null) {
                eventDTO.setEventDate(java.time.LocalDateTime.of(eventDTO.getDate(), eventDTO.getTime()));
            }
            
            eventService.updateEvent(id, eventDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/admin/events";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating event: " + e.getMessage());
            model.addAttribute("eventId", id);
            return "admin/edit-event";
        }
    }

    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    // List all event requests that are waiting for approval
    @GetMapping("/events/requests")
    public String listPendingRequests(@AuthenticationPrincipal User user, Model model) {
        List<Event> pendingEvents = eventService.getPendingEvents();
        model.addAttribute("user", user);
        model.addAttribute("events", pendingEvents);
        return "admin/event-requests";
    }

    // Approve an event request
    @PostMapping("/events/approve/{id}")
    public String approveEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.approveEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving event: " + e.getMessage());
        }
        return "redirect:/admin/events/requests";
    }

    // Reject an event request
    @PostMapping("/events/reject/{id}")
    public String rejectEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.rejectEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting event: " + e.getMessage());
        }
        return "redirect:/admin/events/requests";
    }

    @GetMapping("/users")
    public String manageUsers(@AuthenticationPrincipal User user, Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        return "admin/manage-users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Use the new safe method that only touches the 'active' field
            userService.toggleUserStatus(id);
            
            User user = userService.getUserById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User " + (user.isActive() ? "activated" : "deactivated") + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        UserDTO userDTO = convertToUserDTO(user);
        model.addAttribute("user", userDTO);
        model.addAttribute("roles", Role.values());
        return "admin/edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/edit-user";
        }

        try {
            // Service's updateUser now handles fullName, email, password, active, and role
            userService.updateUser(id, userDTO);
            
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user: " + e.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/edit-user";
        }
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, 
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {
        if (currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account!");
            return "redirect:/admin/users";
        }

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        
        if (event.getEventDate() != null) {
            dto.setEventDate(event.getEventDate());
            dto.setDate(event.getEventDate().toLocalDate());
            dto.setTime(event.getEventDate().toLocalTime());
        }
        
        dto.setPremium(event.isPremium());
        dto.setFeatured(event.isFeatured());
        return dto;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setRegisteredEventCount(user.getRegisteredEvents().size());
        return dto;
    }
}