package ru.practicum.shareit.exception.exceptions;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String unknownState) {
        super(unknownState);
    }
}