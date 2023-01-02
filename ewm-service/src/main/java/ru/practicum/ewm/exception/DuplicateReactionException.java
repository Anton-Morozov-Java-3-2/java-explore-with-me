package ru.practicum.ewm.exception;

public class DuplicateReactionException extends Exception {
    public DuplicateReactionException(String message) {
        super("Reaction update error. The response is duplicated: " + message);
    }
}
