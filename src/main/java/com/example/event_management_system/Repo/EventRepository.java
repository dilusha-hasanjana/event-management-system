package com.example.event_management_system.Repo;

import com.example.event_management_system.Model.Event;
import com.example.event_management_system.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedBy(User createdBy);

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime date);

    List<Event> findByEventDateBetweenOrderByEventDateAsc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByTitle(@Param("keyword") String keyword);

    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByLocation(@Param("keyword") String keyword);

    @Query("SELECT e FROM Event e WHERE e.isPremium = true")
    List<Event> findPremiumEvents();

    @Query("SELECT e FROM Event e WHERE e.isFeatured = true")
    List<Event> findFeaturedEvents();

    @Query("SELECT e FROM Event e JOIN e.attendees u WHERE u.id = :userId")
    List<Event> findEventsByAttendeeId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.createdBy.id = :userId")
    long countByCreatorId(@Param("userId") Long userId);
}