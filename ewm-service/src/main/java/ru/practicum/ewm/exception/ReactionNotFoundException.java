package ru.practicum.ewm.exception;

public class ReactionNotFoundException extends Exception {

    public ReactionNotFoundException(String message) {
        super(message);
    }

    public static String createMessage(Long userId, Long eventId) {
        return String.format("User id=%d reaction to the event id=%d not exists", userId, eventId);
    }
}
