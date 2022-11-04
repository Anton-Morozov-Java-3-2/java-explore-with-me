package ru.practicum.ewm.exception;

public class DuplicateRequestException extends Exception {
    public DuplicateRequestException(String message) {
        super(message);
    }
}
