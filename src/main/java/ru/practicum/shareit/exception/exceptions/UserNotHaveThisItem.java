package ru.practicum.shareit.exception.exceptions;

public class UserNotHaveThisItem extends RuntimeException {
    public UserNotHaveThisItem(final String massage) {
        super(massage);
    }
}