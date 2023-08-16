package ru.practicum.shareit.item.storage.Memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UserNotHaveThisItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.StorageItem;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryStorageItem implements StorageItem {
    private final HashMap<Integer, Item> data = new HashMap<>();
    private final HashMap<User, List<Item>> userItems = new HashMap<>();
    protected Integer id = 0;

    @Override
    public Item addNewItem(ItemDto itemDto, User user) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(++id);
        item.setOwner(user);
        data.put(id, item);
        addUserItem(data.get(id), user);
        return item;
    }

    @Override
    public Item updateItem(ItemDto itemDto, Integer itemId, User user) {
        if (!data.containsKey(itemId)) {
            throw new NotFoundException("Предмет не найден.");
        }
        User owner = data.get(itemId).getOwner();
        Boolean available = itemDto.getAvailable();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        if (!owner.equals(user)) {
            throw new UserNotHaveThisItem();
        }
        Item item = data.get(itemId);
        if (available != null) {
            item.setAvailable(available);
        }
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        item.setId(itemId);
        item.setOwner(user);
        data.put(itemId, item);
        return item;
    }

    @Override
    public Item getItem(Integer itemId) {
        if (!data.containsKey(itemId)) {
            throw new NotFoundException("Предмет не найден.");
        }
        return data.get(itemId);
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<Item>(data.values());
    }

    private void addUserItem(Item item, User user) {
        List<Item> items = userItems.get(user);
        if(items == null) {
            List<Item> newList = new ArrayList<>();
            newList.add(item);
            userItems.put(user, newList);
        } else {
            items.add(item);
        }
    }

    @Override
    public List<Item> ss(User user) {
        return userItems.get(user);
    }
}