package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationObserver implements EventObserver {

    @Override
    public void update(Event event, String action) {
        switch (action) {
            case "CREATED":
                sendEmail("New Event Created: " + event.getTitle(),
                        "Event created at " + event.getLocation());
                break;
            case "UPDATED":
                sendEmail("Event Updated: " + event.getTitle(),
                        "Event details have been updated");
                break;
            case "DELETED":
                sendEmail("Event Cancelled: " + event.getTitle(),
                        "The event has been cancelled");
                break;
            default:
                if (action.startsWith("USER_REGISTERED")) {
                    String username = action.split(":")[1];
                    sendEmail("New Registration", username + " registered for " + event.getTitle());
                }
        }
    }

    @Override
    public String getObserverType() {
        return "EMAIL_NOTIFICATION";
    }

    private void sendEmail(String subject, String body) {
        System.out.println("Email Sent - Subject: " + subject + ", Body: " + body);
    }
}