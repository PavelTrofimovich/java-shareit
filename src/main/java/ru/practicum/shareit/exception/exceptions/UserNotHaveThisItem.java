package ru.practicum.shareit.exception.exceptions;

public class UserNotHaveThisItem extends RuntimeException {
    public UserNotHaveThisItem() {
        super("Пользователь не является владельцем вещи");
    }
}