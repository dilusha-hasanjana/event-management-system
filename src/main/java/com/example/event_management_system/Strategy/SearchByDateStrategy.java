package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class SearchByDateStrategy implements EventSearchStrategy {

    @Override
    public List<Event> search(List<Event> events, String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return List.of();
        }

        try {
            LocalDate searchDate = LocalDate.parse(dateStr);
            return events.stream()
                    .filter(event -> {
                        LocalDateTime eventDate = event.getEventDate();
                        return eventDate != null && eventDate.toLocalDate().equals(searchDate);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Return empty list if date format is invalid (e.g. text instead of yyyy-MM-dd)
            return List.of();
        }
    }

    @Override
    public String getStrategyName() {
        return "DATE_SEARCH";
    }
}