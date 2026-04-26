package com.example.event_management_system.Factory;

import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;

import java.time.LocalDateTime;

/**
 * ========================================
 * DESIGN PATTERN: Factory Method Pattern
 * ========================================
 * Purpose: Creates different types of Event objects without exposing creation logic.
 * How it works: Provides static methods to create Standard, Premium, Featured, or both events.
 * Why we use it: Centralizes object creation - if event creation logic changes,
 *                we only need to update this one class.
 */
public class EventFactory {

    // Create a standard event (no special features)
    public static Event createStandardEvent(String title, String description, String location,
                                            LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(false);
        event.setFeatured(false);
        return event;
    }

    // Create a premium event (VIP access, priority seating)
    public static Event createPremiumEvent(String title, String description, String location,
                                           LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(true);
        event.setFeatured(false);
        return event;
    }

    // Create a featured event (highlighted on the homepage)
    public static Event createFeaturedEvent(String title, String description, String location,
                                            LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(false);
        event.setFeatured(true);
        return event;
    }

    // Create a premium + featured event (both special features)
    public static Event createPremiumFeaturedEvent(String title, String description, String location,
                                                   LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(true);
        event.setFeatured(true);
        return event;
    }
}