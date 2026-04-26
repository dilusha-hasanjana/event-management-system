package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;


public interface EventObserver {
    // Called when something happens to an event
    void update(Event event, String action);

    // Returns the type of this observer (e.g., "EMAIL_NOTIFICATION")
    String getObserverType();
}