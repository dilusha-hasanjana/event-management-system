package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;

import java.util.List;

public interface EventSearchStrategy {
    List<Event> search(List<Event> events, String keyword);
    String getStrategyName();
}