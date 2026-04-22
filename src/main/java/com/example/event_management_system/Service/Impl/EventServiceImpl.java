package com.example.event_management_system.Service.Impl;

import com.example.event_management_system.Decorator.FeaturedEventDecorator;
import com.example.event_management_system.Decorator.PremiumEventDecorator;
import com.example.event_management_system.Dto.EventDTO;
import com.example.event_management_system.Factory.EventFactory;
import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;
import com.example.event_management_system.Observer.EventNotifier;
import com.example.event_management_system.Repo.EventRepository;
import com.example.event_management_system.Service.EventService;
import com.example.event_management_system.Singleton.EventCacheManager;
import com.example.event_management_system.Strategy.EventSearchStrategy;
import com.example.event_management_system.Strategy.SearchByDateStrategy;
import com.example.event_management_system.Strategy.SearchByLocationStrategy;
import com.example.event_management_system.Strategy.SearchByTitleStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventNotifier eventNotifier;
    private final EventCacheManager cacheManager;
    private final Map<String, EventSearchStrategy> searchStrategies;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            EventNotifier eventNotifier,
                            EventCacheManager cacheManager,
                            SearchByTitleStrategy titleStrategy,
                            SearchByDateStrategy dateStrategy,
                            SearchByLocationStrategy locationStrategy) {
        this.eventRepository = eventRepository;
        this.eventNotifier = eventNotifier;
        this.cacheManager = cacheManager;

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

        Event savedEvent = eventRepository.save(event);
        eventNotifier.eventCreated(savedEvent);
        cacheManager.cacheEvent(savedEvent);

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
        cacheManager.cacheEvent(updatedEvent);

        return updatedEvent;
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
        eventNotifier.eventDeleted(event);
        cacheManager.removeCachedEvent(id);
    }

    @Override
    public Event getEventById(Long id) {
        Event cachedEvent = cacheManager.getCachedEvent(id);
        if (cachedEvent != null) {
            return cachedEvent;
        }

        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(
                java.time.LocalDateTime.now()
        );
    }

    @Override
    public List<Event> getEventsByCreator(User user) {
        return eventRepository.findByCreatedBy(user);
    }

    @Override
    public List<Event> searchEvents(String keyword, String strategyType) {
        List<Event> allEvents = getAllEvents();

        EventSearchStrategy strategy = searchStrategies.getOrDefault(
                strategyType != null ? strategyType.toUpperCase() : "TITLE",
                searchStrategies.get("TITLE")
        );

        return strategy.search(allEvents, keyword);
    }

    @Override
    public Event registerUserForEvent(Long eventId, User user) {
        Event event = getEventById(eventId);

        if (event.getAttendees().contains(user)) {
            throw new RuntimeException("User already registered for this event");
        }

        user.registerForEvent(event);
        Event updatedEvent = eventRepository.save(event);
        eventNotifier.userRegistered(updatedEvent, user.getUsername());

        return updatedEvent;
    }

    @Override
    public Event unregisterUserFromEvent(Long eventId, User user) {
        Event event = getEventById(eventId);

        if (!event.getAttendees().contains(user)) {
            throw new RuntimeException("User is not registered for this event");
        }

        user.unregisterFromEvent(event);
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getUserRegisteredEvents(User user) {
        return eventRepository.findEventsByAttendeeId(user.getId());
    }

    @Override
    public List<Event> getPremiumEvents() {
        List<Event> premiumEvents = eventRepository.findPremiumEvents();

        return premiumEvents.stream()
                .map(PremiumEventDecorator::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Event> getFeaturedEvents() {
        List<Event> featuredEvents = eventRepository.findFeaturedEvents();

        return featuredEvents.stream()
                .map(FeaturedEventDecorator::new)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public long getTotalEventCount() {
        return eventRepository.count();
    }
}