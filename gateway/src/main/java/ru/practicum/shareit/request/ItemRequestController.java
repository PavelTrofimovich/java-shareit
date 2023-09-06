package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(value = USER_ID) Integer userId,
                                             @RequestBody @Valid ItemRequestDtoRequest request) {
        log.info("Пользователь id {} создал новый запрос {}", userId, request.getDescription());
        return itemRequestClient.addRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(value = USER_ID) Integer userId) {
        log.info("Пользователь id {} получил список своих запросов", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Integer requestId,
                                             @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Пользователь id {} получил запрос id {}", userId, requestId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(value = USER_ID) Integer userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Пользователь id {} получил список всех запросов", userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
