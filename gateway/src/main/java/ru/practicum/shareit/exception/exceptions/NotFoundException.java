package ru.practicum.shareit.exception.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String massage) {
        super(massage);
    }
}