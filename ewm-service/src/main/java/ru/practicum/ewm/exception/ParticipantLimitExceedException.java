package ru.practicum.ewm.exception;

public class ParticipantLimitExceedException extends Exception {
    public ParticipantLimitExceedException(String message) {
        super(message);
    }

    public static String createMessage() {
        return "Participant limit exceed";
    }
}
