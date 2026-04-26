package com.example.event_management_system.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany(mappedBy = "registeredEvents")
    private Set<User> attendees = new HashSet<>();

    @Column(nullable = false)
    private boolean isPremium = false;

    @Column(nullable = false)
    private boolean isFeatured = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status; // Track if the event is pending, approved, or rejected

    // Custom constructor initializes defaults
    {
        this.createdAt = LocalDateTime.now();
        this.status = EventStatus.PENDING; // New events start as PENDING
    }

    public Event(String title, String description, String location, LocalDateTime eventDate) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
    }

    public void addAttendee(User user) {
        this.attendees.add(user);
    }

    public void removeAttendee(User user) {
        this.attendees.remove(user);
    }
}