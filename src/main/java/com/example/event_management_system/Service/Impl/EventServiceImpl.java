package com.example.event_management_system.Service.Impl;

import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Factory.EventFactory;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.EventStatus;
import com.example.event_management_system.Model.Role;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Observer.EventNotifier;
import com.example.event_management_system.Repo.EventRepository;
import com.example.event_management_system.Repo.UserRepository;
import com.example.event_management_system.Service.EventService;
import com.example.event_management_system.Strategy.EventSearchStrategy;
import com.example.event_management_system.Strategy.SearchByDateStrategy;
import com.example.event_management_system.Strategy.SearchByLocationStrategy;
import com.example.event_management_system.Strategy.SearchByTitleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventNotifier eventNotifier;
    private final Map<String, EventSearchStrategy> searchStrategies;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            EventNotifier eventNotifier,
                            SearchByTitleStrategy titleStrategy,
                            SearchByDateStrategy dateStrategy,
                            SearchByLocationStrategy locationStrategy) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventNotifier = eventNotifier;

        this.searchStrategies = new HashMap<>();
        this.searchStrategies.put("TITLE", titleStrategy);
        this.searchStrategies.put("DATE", dateStrategy);
        this.searchStrategies.put("LOCATION", locationStrategy);
    }

    @Override
    public Event createEvent(EventDTO eventDTO, User createdBy) {
        Event event;

        if (eventDTO.isPremium() && eventDTO.isFeatured()) {
            event = EventFactory.createPremiumFeaturedEvent(
                    eventDTO.getTitle(),
                    eventDTO.getDescription(),
                    eventDTO.getLocation(),
                    eventDTO.getEventDate(),
                    createdBy
            );
        } else if (eventDTO.isPremium()) {
            event = EventFactory.createPremiumEvent(
                    eventDTO.getTitle(),
                    eventDTO.getDescription(),
                    eventDTO.getLocation(),
                    eventDTO.getEventDate(),
                    createdBy
            );
        } else if (eventDTO.isFeatured()) {
            event = EventFactory.createFeaturedEvent(
                    eventDTO.getTitle(),
                    eventDTO.getDescription(),
                    eventDTO.getLocation(),
                    eventDTO.getEventDate(),
                    createdBy
            );
        } else {
            event = EventFactory.createStandardEvent(
                    eventDTO.getTitle(),
                    eventDTO.getDescription(),
                    eventDTO.getLocation(),
                    eventDTO.getEventDate(),
                    createdBy
            );
        }

        
        if (createdBy.getRole() == Role.ADMIN) {
            event.setStatus(EventStatus.APPROVED);
        }

        Event savedEvent = eventRepository.save(event);
        eventNotifier.eventCreated(savedEvent);

        return savedEvent;
    }

    @Override
    public Event updateEvent(Long id, EventDTO eventDTO) {
        Event event = getEventById(id);

        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setLocation(eventDTO.getLocation());
        event.setEventDate(eventDTO.getEventDate());
        event.setPremium(eventDTO.isPremium());
        event.setFeatured(eventDTO.isFeatured());

        Event updatedEvent = eventRepository.save(event);
        eventNotifier.eventUpdated(updatedEvent);

        return updatedEvent;
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
        eventNotifier.eventDeleted(event);
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getUpcomingEvents() {
    
        return eventRepository.findByEventDateAfterAndStatusOrderByEventDateAsc(
                java.time.LocalDateTime.now(),
                EventStatus.APPROVED
        );
    }

    @Override
    public List<Event> getPendingEvents() {
        
        return eventRepository.findByStatus(EventStatus.PENDING);
    }

    @Override
    public void approveEvent(Long id) {
        Event event = getEventById(id);
        event.setStatus(EventStatus.APPROVED);
        eventRepository.save(event);
        
    }

    @Override
    public void rejectEvent(Long id) {
        Event event = getEventById(id);
        event.setStatus(EventStatus.REJECTED);
        eventRepository.save(event);
    }

    @Override
    public List<Event> getEventsByCreator(User user) {
        return eventRepository.findByCreatedBy(user);
    }

    @Override
    public List<Event> searchEvents(String keyword, String strategyType) {
        
        List<Event> searchPool = eventRepository.findByStatus(EventStatus.APPROVED);

        EventSearchStrategy strategy = searchStrategies.getOrDefault(
                strategyType != null ? strategyType.toUpperCase() : "TITLE",
                searchStrategies.get("TITLE")
        );

        return strategy.search(searchPool, keyword);
    }

    @Override
    public Event registerUserForEvent(Long eventId, User user) {
        Event event = getEventById(eventId);
        
        
        User attachedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (event.getAttendees().contains(attachedUser)) {
            throw new RuntimeException("User already registered for this event");
        }

        attachedUser.registerForEvent(event);
        Event updatedEvent = eventRepository.save(event);
        eventNotifier.userRegistered(updatedEvent, attachedUser.getUsername());

        return updatedEvent;
    }

    @Override
    public Event unregisterUserFromEvent(Long eventId, User user) {
        Event event = getEventById(eventId);
        
        
        User attachedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!event.getAttendees().contains(attachedUser)) {
            throw new RuntimeException("User is not registered for this event");
        }

        attachedUser.unregisterFromEvent(event);
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getUserRegisteredEvents(User user) {
        return eventRepository.findEventsByAttendeeId(user.getId());
    }

    @Override
    public long getTotalEventCount() {
        return eventRepository.count();
    }

    

    @Override
    public Event updateOrganizerEvent(Long id, EventDTO eventDTO, User organizer) {
        Event event = getEventById(id);

        
        if (!event.getCreatedBy().getId().equals(organizer.getId())) {
            throw new RuntimeException("You can only edit events you created.");
        }

        // Validate status
        if (event.getStatus() != EventStatus.PENDING) {
            throw new RuntimeException("Only PENDING events can be edited. This event is " + event.getStatus() + ".");
        }

        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setLocation(eventDTO.getLocation());
        event.setEventDate(eventDTO.getEventDate());

        Event updatedEvent = eventRepository.save(event);
        eventNotifier.eventUpdated(updatedEvent);
        log.info("Organizer '{}' updated PENDING event: {}", organizer.getUsername(), event.getTitle());

        return updatedEvent;
    }

    @Override
    public void deleteOrganizerEvent(Long id, User organizer) {
        Event event = getEventById(id);

        // Validate ownership
        if (!event.getCreatedBy().getId().equals(organizer.getId())) {
            throw new RuntimeException("You can only delete events you created.");
        }

        // Validate status
        if (event.getStatus() != EventStatus.PENDING) {
            throw new RuntimeException("Only PENDING events can be deleted. This event is " + event.getStatus() + ".");
        }

        eventRepository.delete(event);
        eventNotifier.eventDeleted(event);
        log.info("Organizer '{}' deleted PENDING event: {}", organizer.getUsername(), event.getTitle());
    }
}