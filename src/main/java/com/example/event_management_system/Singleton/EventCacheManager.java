package com.example.event_management_system.Singleton;

import com.example.event_management_system.Model.Event;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventCacheManager {

    private static volatile EventCacheManager instance;

    private final Map<Long, Event> eventCache = new ConcurrentHashMap<>();
    private final Map<String, Object> metadataCache = new ConcurrentHashMap<>();

    private EventCacheManager() {}

    public static EventCacheManager getInstance() {
        if (instance == null) {
            synchronized (EventCacheManager.class) {
                if (instance == null) {
                    instance = new EventCacheManager();
                }
            }
        }
        return instance;
    }

    public void cacheEvent(Event event) {
        if (event != null && event.getId() != null) {
            eventCache.put(event.getId(), event);
        }
    }

    public Event getCachedEvent(Long eventId) {
        return eventCache.get(eventId);
    }

    public void removeCachedEvent(Long eventId) {
        eventCache.remove(eventId);
    }

    public void clearCache() {
        eventCache.clear();
        metadataCache.clear();
    }

    public int getCacheSize() {
        return eventCache.size();
    }

    public boolean isCached(Long eventId) {
        return eventCache.containsKey(eventId);
    }

    public void cacheMetadata(String key, Object value) {
        metadataCache.put(key, value);
    }

    public Object getCachedMetadata(String key) {
        return metadataCache.get(key);
    }
}