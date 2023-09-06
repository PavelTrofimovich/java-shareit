package ru.practicum.shareit.exception.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(final String massage) {
        super(massage);
    }
}