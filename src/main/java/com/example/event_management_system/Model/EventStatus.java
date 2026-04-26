package com.example.event_management_system.Model;
// Simple enum to track the status of an event request
public enum EventStatus {
    PENDING,   // Waiting for admin approval
    APPROVED,  // Confirmed and visible to everyone
    REJECTED   // Declined by admin
}
