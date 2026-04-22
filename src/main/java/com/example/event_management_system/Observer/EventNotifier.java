package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventNotifier {
    private List<EventObserver> observers = new ArrayList<>();

    public void attach(EventObserver observer) {
        observers.add(observer);
    }

    public void detach(EventObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Event event, String action) {
        for (EventObserver observer : observers) {
            observer.update(event, action);
        }
    }

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