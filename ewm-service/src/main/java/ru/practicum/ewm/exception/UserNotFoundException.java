package ru.practicum.ewm.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

    public static String createMessage(Long id) {
        return String.format("User with id= %d not found.", id);
    }
}
