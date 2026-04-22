package com.example.event_management_system.Observer;

import com.example.event_management_system.Model.Event;

public interface EventObserver {
    void update(Event event, String action);
    String getObserverType();
}