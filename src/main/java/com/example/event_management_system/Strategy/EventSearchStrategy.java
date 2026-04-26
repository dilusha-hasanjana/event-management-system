package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;

import java.util.List;

/**
 * ========================================
 * DESIGN PATTERN: Strategy Pattern (Interface)
 * ========================================
 * Purpose: Defines a common interface for different search algorithms.
 * How it works: Each search type (by title, location, date) implements this interface.
 * Why we use it: Allows switching search algorithms at runtime without changing the service code.
 */
public interface EventSearchStrategy {
    // Perform the search using this strategy's algorithm
    List<Event> search(List<Event> events, String keyword);

    // Return the name of this search strategy
    String getStrategyName();
}