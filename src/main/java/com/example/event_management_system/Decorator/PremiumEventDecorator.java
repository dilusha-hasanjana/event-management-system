package com.example.event_management_system.Decorator;

import com.example.event_management_system.Model.Event;

public class PremiumEventDecorator extends EventDecorator {

    public PremiumEventDecorator(Event event) {
        super(event);
        this.setPremium(true);
    }

    @Override
    public String getDescription() {
        return "⭐ PREMIUM ⭐\n" + super.getDescription() +
                "\n\nPremium Benefits:\n- Priority Seating\n- VIP Access\n- Exclusive Materials";
    }

    @Override
    public String getTitle() {
        return "★ " + decoratedEvent.getTitle() + " ★";
    }
}