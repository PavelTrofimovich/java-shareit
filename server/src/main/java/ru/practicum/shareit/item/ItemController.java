package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addNewItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Запрос на добавление Item {} с userId {}", itemDto, userId);
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = USER_ID) Integer userId,
                              @RequestBody ItemDto itemDto, @PathVariable Integer itemId) {
        log.info("Запрос на обновление Item с ID {}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemCommentDto getItem(@RequestHeader(value = USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("Запрос на вывод Item с ID {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemCommentDto> getItems(@RequestHeader(value = USER_ID) Integer userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на вывод Items пользователя с ID {}", userId);
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(value = USER_ID) Integer userId,
                                     @RequestParam("text") String search,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на поиск Item по тексту: {}", search);
        return itemService.searchItems(userId, search, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(value = USER_ID) Integer userId,
                                  @PathVariable Integer itemId,
                                  @RequestBody CommentDtoRequest commentDtoRequest) {
        log.info("Коментарий добавлен");
        return itemService.postComment(commentDtoRequest, itemId, userId);
    }
}