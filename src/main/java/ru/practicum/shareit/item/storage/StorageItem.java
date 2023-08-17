package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface StorageItem {
    Item addNewItem(ItemDto itemDto, User user);

    Item updateItem(ItemDto itemDto, Integer itemId, User user);

    Item getItem(Integer itemId);

    List<Item> getItems();

    List<Item> getUserItems(User user);
}