package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================
 * DESIGN PATTERN: Strategy Pattern (Concrete Strategy)
 * ========================================
 * Purpose: Searches events by matching the location field.
 * How it works: Filters events where the location contains the search keyword (case-insensitive).
 */
@Component
public class SearchByLocationStrategy implements EventSearchStrategy {

    @Override
    public List<Event> search(List<Event> events, String keyword) {
        return events.stream()
                .filter(event -> event.getLocation().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "LOCATION_SEARCH";
    }
}