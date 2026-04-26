package com.example.event_management_system.Controller;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Service.EventService;
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
@RequestMapping("/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final EventService eventService;

    // Show the Organizer Dashboard
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        // Get only events created by this organizer
        List<Event> myEvents = eventService.getEventsByCreator(user);
        model.addAttribute("events", myEvents);
        model.addAttribute("user", user);
        return "organizer/dashboard";
    }

    // Show the form to request a new event
    @GetMapping("/events/new")
    public String showRequestForm(Model model) {
        model.addAttribute("event", new EventDTO());
        return "organizer/request-event";
    }

    // Handle the submission of a new event request
    @PostMapping("/events/request")
    public String requestEvent(@Valid @ModelAttribute("event") EventDTO eventDTO,
                               BindingResult result,
                               @AuthenticationPrincipal User user,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            return "organizer/request-event";
        }

        try {
            // Create the event (it will be PENDING by default in the service logic)
            eventService.createEvent(eventDTO, user);
            redirectAttributes.addFlashAttribute("successMessage", "Event request submitted successfully! Waiting for admin approval.");
            return "redirect:/organizer/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error submitting request: " + e.getMessage());
            return "organizer/request-event";
        }
    }
}
