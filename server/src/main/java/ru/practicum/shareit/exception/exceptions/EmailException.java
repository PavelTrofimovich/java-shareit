package ru.practicum.shareit.exception.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(final String massage) {
        super(massage);
    }
}