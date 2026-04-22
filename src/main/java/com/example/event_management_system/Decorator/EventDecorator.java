package com.example.event_management_system.Decorator;

import com.example.event_management_system.Model.Event;

public abstract class EventDecorator extends Event {
    protected Event decoratedEvent;

    public EventDecorator(Event event) {
        super();
        this.decoratedEvent = event;
        this.setId(event.getId());
        this.setTitle(event.getTitle());
        this.setDescription(event.getDescription());
        this.setLocation(event.getLocation());
        this.setEventDate(event.getEventDate());
        this.setCreatedBy(event.getCreatedBy());
        this.setCreatedAt(event.getCreatedAt());
    }

    @Override
    public String getDescription() {
        return decoratedEvent.getDescription();
    }
}