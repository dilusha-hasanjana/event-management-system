package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================
 * DESIGN PATTERN: Strategy Pattern (Concrete Strategy)
 * ========================================
 * Purpose: Searches events by matching the event date.
 * How it works: Filters events where the date matches the search date string (format: yyyy-MM-dd).
 */
@Component
public class SearchByDateStrategy implements EventSearchStrategy {

    @Override
    public List<Event> search(List<Event> events, String dateStr) {
        LocalDate searchDate = LocalDate.parse(dateStr);
        return events.stream()
                .filter(event -> {
                    LocalDateTime eventDate = event.getEventDate();
                    return eventDate.toLocalDate().equals(searchDate);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "DATE_SEARCH";
    }
}