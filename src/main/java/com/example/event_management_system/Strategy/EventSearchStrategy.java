package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;

import java.util.List;


public interface EventSearchStrategy {
    // Perform the search using this strategy's algorithm
    List<Event> search(List<Event> events, String keyword);

    // Return the name of this search strategy
    String getStrategyName();
}