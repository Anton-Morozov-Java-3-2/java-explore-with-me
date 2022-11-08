package ru.practicum.ewm.exception;

public class EventNotFoundException extends Exception {
    public EventNotFoundException(String message) {
        super(message);
    }

    public static String createMessage(Long eventId) {
        return String.format("Event id=%d not found", eventId);
    }
}
