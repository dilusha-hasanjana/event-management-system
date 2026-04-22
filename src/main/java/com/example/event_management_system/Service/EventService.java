package com.example.event_management_system.Service;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;

import java.util.List;

public interface EventService {
    Event createEvent(EventDTO eventDTO, User createdBy);
    Event updateEvent(Long id, EventDTO eventDTO);
    void deleteEvent(Long id);
    Event getEventById(Long id);
    List<Event> getAllEvents();
    List<Event> getUpcomingEvents();
    List<Event> getEventsByCreator(User user);
    List<Event> searchEvents(String keyword, String strategy);
    Event registerUserForEvent(Long eventId, User user);
    Event unregisterUserFromEvent(Long eventId, User user);
    List<Event> getUserRegisteredEvents(User user);
    List<Event> getPremiumEvents();
    List<Event> getFeaturedEvents();
    long getTotalEventCount();
}