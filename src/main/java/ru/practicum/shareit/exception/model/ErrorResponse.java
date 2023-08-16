package ru.practicum.shareit.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorResponse {

    @Getter
    private final String error;
}