package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.StorageItem;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.StorageUser;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final StorageItem storageItem;
    private final StorageUser storageUser;

    @Override
    public ItemDto addNewItem(ItemDto itemDto, Integer userId) {
        User user = storageUser.getUser(userId);
        return ItemMapper.toItemDto(storageItem.addNewItem(itemDto, user));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        User user = storageUser.getUser(userId);
        return ItemMapper.toItemDto(storageItem.updateItem(itemDto, itemId, user));
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return ItemMapper.toItemDto(storageItem.getItem(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        User user = storageUser.getUser(userId);
        List<Item> items = storageItem.getItems();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().equals(user)) {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItems(Integer userId, String search) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        String description;
        String name;
        if (search.isEmpty()) {
            return itemDtoList;
        }
        search = search.toLowerCase();
        for (Item item : storageItem.getItems()) {
            description = item.getDescription().toLowerCase();
            name = item.getName().toLowerCase();
            if ((name.contains(search) || description.contains(search)) && item.getAvailable()) {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
        }
        return itemDtoList;
    }
}