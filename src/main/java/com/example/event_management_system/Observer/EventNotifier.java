package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ========================================
 * DESIGN PATTERN: Observer Pattern (Subject/Notifier)
 * ========================================
 * Purpose: Manages a list of observers and notifies them when event actions happen.
 * How it works: Spring injects all EventObserver beans, then notifies them automatically.
 * Why we use it: When an event is created/updated/deleted, all observers are notified
 *                without the service needing to know about each observer directly.
 */
@Component
public class EventNotifier {
    // List of all registered observers
    private final List<EventObserver> observers;

    @Autowired
    public EventNotifier(List<EventObserver> observers) {
        this.observers = new ArrayList<>(observers != null ? observers : List.of());
    }

    // Register a new observer to receive notifications
    public void attach(EventObserver observer) {
        observers.add(observer);
    }

    // Remove an observer from the notification list
    public void detach(EventObserver observer) {
        observers.remove(observer);
    }

    // Notify all observers about an action on an event
    public void notifyObservers(Event event, String action) {
        for (EventObserver observer : observers) {
            observer.update(event, action);
        }
    }

    // Convenience methods for common event actions
    public void eventCreated(Event event) {
        notifyObservers(event, "CREATED");
    }

    public void eventUpdated(Event event) {
        notifyObservers(event, "UPDATED");
    }

    public void eventDeleted(Event event) {
        notifyObservers(event, "DELETED");
    }

    public void userRegistered(Event event, String username) {
        notifyObservers(event, "USER_REGISTERED:" + username);
    }
}