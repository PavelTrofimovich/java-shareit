package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface StorageUser {
    User addUser(UserDto userDto);

    User updateUser(UserDto userDto, Integer id);

    User getUser(Integer id);

    void deleteUser(Integer id);

    List<User> getAllUsers();
}