package com.example.event_management_system.Controller;

import com.example.event_management_system.Dto.UserDTO;
import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully!");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Check if passwords match
        if (userDTO.getPassword() != null && !userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            model.addAttribute("passwordError", "Passwords do not match!");
            return "auth/register";
        }

        if (userService.existsByUsername(userDTO.getUsername())) {
            model.addAttribute("usernameError", "Username already exists!");
            return "auth/register";
        }

        if (userService.existsByEmail(userDTO.getEmail())) {
            model.addAttribute("emailError", "Email already registered!");
            return "auth/register";
        }

        try {
            userService.createUser(userDTO, Role.USER);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }



    @GetMapping("/")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin/dashboard";
            } else if (user.getRole() == Role.ORGANIZER) {
                return "redirect:/organizer/dashboard"; // Redirect Organizers to their dashboard
            } else {
                return "redirect:/user/dashboard";
            }
        }
        return "redirect:/login";
    }
}