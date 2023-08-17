package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.StorageUser;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final StorageUser storageUser;

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(storageUser.addUser(userDto));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        return UserMapper.toUserDto(storageUser.updateUser(userDto, id));
    }

    @Override
    public UserDto getUser(Integer id) {
        return UserMapper.toUserDto(storageUser.getUser(id));
    }

    @Override
    public void deleteUser(Integer id) {
        storageUser.deleteUser(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        storageUser.getAllUsers().forEach(user -> userDtoList.add(UserMapper.toUserDto(user)));
        return userDtoList;
    }
}