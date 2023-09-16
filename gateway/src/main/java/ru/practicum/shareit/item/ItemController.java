package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addNewItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Creating Item {}, userId=, {}", itemDto, userId);
        return itemClient.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                              @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Запрос на обновление Item с ID {}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(value = USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("Запрос на вывод Item с ID {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(value = USER_ID) Integer userId,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         @Positive int size) {
        log.info("Запрос на вывод Items пользователя с ID {}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(value = USER_ID) Integer userId,
                                     @RequestParam("text") String search,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на поиск Item по тексту: {}", search);
        return itemClient.searchItems(userId, search, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(value = USER_ID) Integer userId,
                                  @PathVariable Integer itemId,
                                  @Valid @RequestBody CommentDtoRequest commentDtoRequest) {
        log.info("Коментарий добавлен");
        return itemClient.postComment(userId, itemId, commentDtoRequest);
    }
}