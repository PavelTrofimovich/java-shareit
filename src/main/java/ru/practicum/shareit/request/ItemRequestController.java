package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestBody @Valid ItemRequestDtoRequest request,
                                     @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Пользователь id {} создал новый запрос {}", userId, request.getDescription());
        return itemRequestService.addRequest(request, userId);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getUserRequests(@RequestHeader(value = USER_ID) Integer userId) {
        log.info("Пользователь id {} получил список своих запросов", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequest(@PathVariable Integer requestId,
                                             @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Пользователь id {} получил запрос id {}", userId, requestId);
        return itemRequestService.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllRequests(@RequestHeader(value = USER_ID) Integer userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Пользователь id {} получил список всех запросов", userId);
        return itemRequestService.getAllRequests(userId, from, size);
    }
}
