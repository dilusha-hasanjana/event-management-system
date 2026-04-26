package com.example.event_management_system.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventDTO {
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 200, message = "Location must be between 2 and 200 characters")
    private String location;

    @NotNull(message = "Event date is required")
    private java.time.LocalDate date;

    @NotNull(message = "Event time is required")
    private java.time.LocalTime time;

    // Combined date and time for backend compatibility
    private LocalDateTime eventDate;

    private String createdByUsername;
    private boolean premium;
    private boolean featured;
    private int attendeeCount;
}