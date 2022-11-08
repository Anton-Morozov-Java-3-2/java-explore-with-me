package ru.practicum.ewm.exception;

public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public static String createMessage(long categoryId) {
        return String.format("Category id=%d not found", categoryId);
    }
}
