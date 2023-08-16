package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Запрос на добавление Item {} с userId {}", itemDto, userId);
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                              @RequestHeader(value = USER_ID) Integer userId) {
        log.info("Запрос на обновление Item с ID {}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        log.info("Запрос на вывод Item с ID {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(value = USER_ID) Integer userId) {
        log.info("Запрос на вывод Items пользователя с ID {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(value = USER_ID) Integer userId,
                                     @RequestParam("text") String search) {
        log.info("Запрос на поиск Item по тексту: {}", search);
        return itemService.searchItems(userId, search);
    }
}