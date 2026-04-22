package com.example.event_management_system.Decorator;

import com.example.event_management_system.Model.Event;

public class FeaturedEventDecorator extends EventDecorator {

    public FeaturedEventDecorator(Event event) {
        super(event);
        this.setFeatured(true);
    }

    @Override
    public String getDescription() {
        return "🔥 FEATURED EVENT 🔥\n" + super.getDescription() +
                "\n\nThis is a featured faculty event. Limited seats available!";
    }

    @Override
    public String getTitle() {
        return "【FEATURED】" + decoratedEvent.getTitle();
    }
}