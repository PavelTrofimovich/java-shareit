package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer id);

    UserDto getUser(Integer id);

    void deleteUser(Integer id);

    List<UserDto> getAllUsers();
}