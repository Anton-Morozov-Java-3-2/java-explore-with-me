package ru.practicum.ewm.exception;

public class CompilationNotFoundException extends Exception {
    public CompilationNotFoundException(String message) {
        super(message);
    }

    public static String createMessage(Long compId) {
        return String.format("Compilation id=%d not found", compId);
    }
}
