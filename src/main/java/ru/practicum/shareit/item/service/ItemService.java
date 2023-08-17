package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getUserItems(Integer userId);

    List<ItemDto> searchItems(Integer userId, String search);
}