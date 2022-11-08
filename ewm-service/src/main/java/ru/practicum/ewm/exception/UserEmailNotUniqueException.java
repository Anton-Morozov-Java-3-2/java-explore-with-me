package ru.practicum.ewm.exception;

public class UserEmailNotUniqueException extends Exception {
    public UserEmailNotUniqueException(String message) {
        super(message);
    }
}
