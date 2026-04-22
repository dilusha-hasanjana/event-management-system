package com.example.event_management_system.Factory;

import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;

import java.time.LocalDateTime;

public class EventFactory {

    public static Event createStandardEvent(String title, String description, String location,
                                            LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(false);
        event.setFeatured(false);
        return event;
    }

    public static Event createPremiumEvent(String title, String description, String location,
                                           LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(true);
        event.setFeatured(false);
        return event;
    }

    public static Event createFeaturedEvent(String title, String description, String location,
                                            LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(false);
        event.setFeatured(true);
        return event;
    }

    public static Event createPremiumFeaturedEvent(String title, String description, String location,
                                                   LocalDateTime eventDate, User createdBy) {
        Event event = new Event(title, description, location, eventDate);
        event.setCreatedBy(createdBy);
        event.setPremium(true);
        event.setFeatured(true);
        return event;
    }
}