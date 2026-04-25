package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;

/**
 * ========================================
 * DESIGN PATTERN: Observer Pattern (Interface)
 * ========================================
 * Purpose: Defines a contract for objects that want to be notified when events happen.
 * How it works: Any class that implements this interface will receive notifications.
 * Why we use it: Allows loose coupling - the notifier doesn't need to know who is listening.
 */
public interface EventObserver {
    // Called when something happens to an event
    void update(Event event, String action);

    // Returns the type of this observer (e.g., "EMAIL_NOTIFICATION")
    String getObserverType();
}