package ru.practicum.ewm.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDateNotValidException extends Exception {
    public EventDateNotValidException(String message) {
        super(message);
    }

    public static String createMessage(LocalDateTime dateTime) {
        return "Not valid event date: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
