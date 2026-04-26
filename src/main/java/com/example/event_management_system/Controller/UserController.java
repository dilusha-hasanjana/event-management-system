package com.example.event_management_system.Controller;

import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final EventService eventService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        List<Event> myEvents = eventService.getUserRegisteredEvents(user);
        List<Event> upcomingEvents = eventService.getUpcomingEvents();

        model.addAttribute("user", user);
        model.addAttribute("myEvents", myEvents);
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("registeredCount", myEvents.size());
        return "user/dashboard";
    }

    @GetMapping("/events")
    public String viewEvents(@AuthenticationPrincipal User user, Model model) {
        List<Event> allEvents = eventService.getUpcomingEvents();
        List<Event> myEvents = eventService.getUserRegisteredEvents(user);

        model.addAttribute("user", user);
        model.addAttribute("events", allEvents);
        model.addAttribute("myEventIds", myEvents.stream().map(Event::getId).toList());
        return "user/view-events";
    }

    @GetMapping("/events/search")
    public String searchEvents(@AuthenticationPrincipal User user,
                               @RequestParam String keyword,
                               @RequestParam(required = false, defaultValue = "TITLE") String strategy,
                               Model model) {
        List<Event> searchResults = eventService.searchEvents(keyword, strategy);
        List<Event> myEvents = eventService.getUserRegisteredEvents(user);
        
        model.addAttribute("user", user);
        model.addAttribute("events", searchResults);
        model.addAttribute("myEventIds", myEvents.stream().map(Event::getId).toList());
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchStrategy", strategy);
        return "user/view-events";
    }

    @PostMapping("/events/register/{eventId}")
    public String registerForEvent(@PathVariable Long eventId,
                                   @AuthenticationPrincipal User user,
                                   RedirectAttributes redirectAttributes) {
        try {
            eventService.registerUserForEvent(eventId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered for the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
        }
        return "redirect:/user/events";
    }

    @PostMapping("/events/unregister/{eventId}")
    public String unregisterFromEvent(@PathVariable Long eventId,
                                      @AuthenticationPrincipal User user,
                                      RedirectAttributes redirectAttributes) {
        try {
            eventService.unregisterUserFromEvent(eventId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully unregistered from the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unregistration failed: " + e.getMessage());
        }
        return "redirect:/user/events";
    }

    @GetMapping("/my-events")
    public String myEvents(@AuthenticationPrincipal User user, Model model) {
        List<Event> myEvents = eventService.getUserRegisteredEvents(user);
        model.addAttribute("user", user);
        model.addAttribute("events", myEvents);
        return "user/my-events";
    }
}