package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ========================================
 * DESIGN PATTERN: Observer Pattern (Concrete Observer)
 * ========================================
 * Purpose: A concrete observer that sends email notifications when events change.
 * How it works: Implements EventObserver interface and reacts to event actions.
 * Why we use it: Separates notification logic from business logic.
 *                We can add more observers (SMS, Push, etc.) without changing the notifier.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationObserver implements EventObserver {

    private final EmailService emailService;

    @Override
    public void update(Event event, String action) {
        if (event == null || event.getCreatedBy() == null) {
            return;
        }

        String to = event.getCreatedBy().getEmail();
        log.info("Event notification - Title: {}, CreatedBy: {}, Email: {}", event.getTitle(), event.getCreatedBy(), to);
        if (to == null || to.isBlank()) {
            return;
        }

        switch (action) {
            case "CREATED":
                emailService.sendEmail(to,
                        "New Event Created: " + event.getTitle(),
                        "Your event '" + event.getTitle() + "' has been created at " + event.getLocation() + ".");
                break;
            case "UPDATED":
                emailService.sendEmail(to,
                        "Event Updated: " + event.getTitle(),
                        "Your event '" + event.getTitle() + "' has been updated.");
                break;
            case "DELETED":
                emailService.sendEmail(to,
                        "Event Cancelled: " + event.getTitle(),
                        "Your event '" + event.getTitle() + "' has been cancelled.");
                break;
            default:
                if (action.startsWith("USER_REGISTERED:")) {
                    String username = action.split(":", 2)[1];
                    emailService.sendEmail(to,
                            "New Registration for " + event.getTitle(),
                            username + " has registered for your event '" + event.getTitle() + "'.");
                }
        }
    }

    @Override
    public String getObserverType() {
        return "EMAIL_NOTIFICATION";
    }

}
