package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDtoRequest itemRequestDto, Integer userId);

    List<ItemRequestDtoResponse> getUserRequests(Integer userId);

    List<ItemRequestDtoResponse> getAllRequests(Integer userId, int from, int size);

    ItemRequestDtoResponse getRequest(Integer userId, Integer requestId);
}
