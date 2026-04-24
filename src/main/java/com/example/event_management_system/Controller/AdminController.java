package com.example.event_management_system.Controller;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Model.*;
import com.example.event_management_system.Service.EventService;
import com.example.event_management_system.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public AdminController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEvents", eventService.getTotalEventCount());
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("adminCount", userService.getAdminCount());
        model.addAttribute("regularUserCount", userService.getRegularUserCount());
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents().size());
        return "admin/dashboard";
    }

    @GetMapping("/events/get")
    public String showAddEventForm(Model model) {
        model.addAttribute("event", new EventDTO());
        return "admin/add-event";
    }

    @PostMapping("/events/add")
    public String addEvent(@Valid @ModelAttribute("event") EventDTO eventDTO,
                           BindingResult result,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "admin/add-event";
        }

        try {
            eventService.createEvent(eventDTO, user);
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            return "redirect:/admin/events/get";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "admin/add-event";
        }
    }

    @GetMapping("/events")
    public String listEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
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

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            user.setActive(!user.isActive());
            UserDTO userDTO = new UserDTO();
            userDTO.setActive(user.isActive());
            userService.updateUser(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User " + (user.isActive() ? "activated" : "deactivated") + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setEventDate(event.getEventDate());
        dto.setPremium(event.isPremium());
        dto.setFeatured(event.isFeatured());
        return dto;
    }
}