package com.example.event_management_system.Controller;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.EventStatus;
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

    // ---- READ: Show the Organizer Dashboard (stats only) ----
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        List<Event> myEvents = eventService.getEventsByCreator(user);
        
        long pendingCount = myEvents.stream().filter(e -> e.getStatus() == EventStatus.PENDING).count();
        long approvedCount = myEvents.stream().filter(e -> e.getStatus() == EventStatus.APPROVED).count();
        long rejectedCount = myEvents.stream().filter(e -> e.getStatus() == EventStatus.REJECTED).count();

        model.addAttribute("user", user);
        model.addAttribute("totalEvents", myEvents.size());
        model.addAttribute("pendingEvents", pendingCount);
        model.addAttribute("approvedEvents", approvedCount);
        model.addAttribute("rejectedEvents", rejectedCount);
        
        return "organizer/dashboard";
    }

    // ---- READ: Show the Manage Events page (table only) ----
    @GetMapping("/events")
    public String manageEvents(@AuthenticationPrincipal User user, Model model) {
        List<Event> myEvents = eventService.getEventsByCreator(user);
        model.addAttribute("events", myEvents);
        model.addAttribute("user", user);
        return "organizer/manage-events";
    }

    // ---- READ: View full details of a specific event ----
    @GetMapping("/events/view/{id}")
    public String viewEvent(@PathVariable Long id,
                            @AuthenticationPrincipal User user,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(id);

            // Verify ownership
            if (!event.getCreatedBy().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only view events you created.");
                return "redirect:/organizer/dashboard";
            }

            model.addAttribute("event", event);
            return "organizer/view-event";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found: " + e.getMessage());
            return "redirect:/organizer/dashboard";
        }
    }

    // ---- CREATE: Show the form to request a new event ----
    @GetMapping("/events/new")
    public String showRequestForm(Model model) {
        model.addAttribute("event", new EventDTO());
        return "organizer/request-event";
    }

    // ---- CREATE: Handle the submission of a new event request ----
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

    // ---- UPDATE: Show the edit form for a PENDING event ----
    @GetMapping("/events/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal User user,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(id);

            // Verify ownership
            if (!event.getCreatedBy().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only edit events you created.");
                return "redirect:/organizer/dashboard";
            }

            // Verify status
            if (event.getStatus() != EventStatus.PENDING) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only PENDING events can be edited. This event is " + event.getStatus() + ".");
                return "redirect:/organizer/dashboard";
            }

            EventDTO eventDTO = convertToDTO(event);
            model.addAttribute("event", eventDTO);
            model.addAttribute("eventId", id);
            return "organizer/edit-event";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found: " + e.getMessage());
            return "redirect:/organizer/dashboard";
        }
    }

    // ---- UPDATE: Process the edit form submission ----
    @PostMapping("/events/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("event") EventDTO eventDTO,
                              BindingResult result,
                              @AuthenticationPrincipal User user,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("eventId", id);
            return "organizer/edit-event";
        }

        try {
            eventService.updateOrganizerEvent(id, eventDTO, user);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/organizer/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating event: " + e.getMessage());
            model.addAttribute("eventId", id);
            return "organizer/edit-event";
        }
    }

    // ---- DELETE: Delete a PENDING event ----
    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id,
                              @AuthenticationPrincipal User user,
                              RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteOrganizerEvent(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Event request deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
        }
        return "redirect:/organizer/dashboard";
    }

    // ---- Helper: Convert Event entity to DTO for forms ----
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
