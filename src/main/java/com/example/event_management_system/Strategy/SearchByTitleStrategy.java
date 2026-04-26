package com.example.event_management_system.Strategy;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class SearchByTitleStrategy implements EventSearchStrategy {

    @Override
    public List<Event> search(List<Event> events, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String lowerKeyword = keyword.toLowerCase();
        return events.stream()
                .filter(event -> event.getTitle() != null && 
                        event.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "TITLE_SEARCH";
    }
}