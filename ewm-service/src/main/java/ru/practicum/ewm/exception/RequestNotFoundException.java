package ru.practicum.ewm.exception;

public class RequestNotFoundException extends Exception {
    public RequestNotFoundException(String message) {
        super(message);
    }

    public static String createMessage(Long reqId) {
        return String.format("Request id=%d not found", reqId);
    }
}
