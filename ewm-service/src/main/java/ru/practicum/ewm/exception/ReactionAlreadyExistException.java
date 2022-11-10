package ru.practicum.ewm.exception;

public class ReactionAlreadyExistException extends Exception {
    public ReactionAlreadyExistException(String message) {
        super(message);
    }

    public static String createMessage(Long userId, Long eventId) {
        return String.format("user id=%d reaction to the event id=%d already exists", userId, eventId);
    }
}
