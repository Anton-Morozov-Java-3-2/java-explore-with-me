package ru.practicum.ewm.exception;

public class UserAccessException extends Exception {
    public UserAccessException(String message) {
        super(message);
    }

    public static String createMessage(Long userId) {
        return String.format("User id=% does not have access", userId);
    }
}
