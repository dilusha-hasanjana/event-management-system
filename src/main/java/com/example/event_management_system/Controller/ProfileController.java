package com.example.event_management_system.Controller;

import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.EventStatus;
import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Service.EventService;
import com.example.event_management_system.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;
    private final EventService eventService;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal User user, Model model) {
        // Refresh user from DB to get latest stats
        User currentUser = userService.getUserById(user.getId());
        
        UserDTO userDTO = convertToDTO(currentUser);
        model.addAttribute("user", userDTO);
        model.addAttribute("rawUser", currentUser);

        // Add achievement stats
        if (currentUser.getRole() == Role.ORGANIZER) {
            List<Event> myEvents = eventService.getEventsByCreator(currentUser);
            long approved = myEvents.stream().filter(e -> e.getStatus() == EventStatus.APPROVED).count();
            model.addAttribute("totalProposed", myEvents.size());
            model.addAttribute("totalApproved", approved);
        } else if (currentUser.getRole() == Role.USER) {
            model.addAttribute("totalRegistered", currentUser.getRegisteredEvents().size());
        }

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") UserDTO userDTO,
                               BindingResult result,
                               @AuthenticationPrincipal User user,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.getUserById(user.getId());
            model.addAttribute("rawUser", currentUser);
            return "profile";
        }

        // Check if passwords match (if a new password is being set)
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                model.addAttribute("passwordError", "Passwords do not match!");
                User currentUser = userService.getUserById(user.getId());
                model.addAttribute("rawUser", currentUser);
                return "profile";
            }
        }

        try {
            userService.updateProfile(user.getId(), userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            log.error("Error updating profile for user {}: {}", user.getUsername(), e.getMessage());
            model.addAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            User currentUser = userService.getUserById(user.getId());
            model.addAttribute("rawUser", currentUser);
            return "profile";
        }
    }

    private UserDTO convertToDTO(User user) {
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
