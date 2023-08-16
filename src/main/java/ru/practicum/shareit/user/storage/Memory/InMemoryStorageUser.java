package ru.practicum.shareit.user.storage.Memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.StorageUser;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryStorageUser implements StorageUser {
    private final HashMap<Integer, User> data = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    protected Integer id = 0;

    @Override
    public User addUser(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            log.error("ValidationException: Пользователь с таким email уже существует.");
            throw new EmailException("Пользователь с таким email уже существует.");
        }
        emails.add(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        user.setId(++id);
        data.put(id, user);
        return user;
    }

    @Override
    public User updateUser(UserDto userDto, Integer id) {
        if (!data.containsKey(id)) {
            log.error("NotFoundException: Пользователь не найден.");
            throw new NotFoundException("Пользователь не найден.");
        }
        User user = data.get(id);
        String email = user.getEmail();
        emails.remove(email);
        if (emails.contains(userDto.getEmail())) {
            log.error("ValidationException: Пользователь с таким email уже существует.");
            throw new EmailException("Пользователь с таким email уже существует.");
        }
        if (userDto.getEmail() == null) {
            emails.add(email);
        } else {
            emails.add(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        user.setId(id);
        data.put(id, user);
        return user;
    }

    @Override
    public User getUser(Integer id) {
        if (!data.containsKey(id)) {
            log.error("NotFoundException: Пользователь не найден.");
            throw new NotFoundException("Пользователь не найден.");
        }
        return data.get(id);
    }

    @Override
    public void deleteUser(Integer id) {
        if (!data.containsKey(id)) {
            log.error("NotFoundException: Пользователь не найден.");
            throw new NotFoundException("Пользователь не найден.");
        }
        emails.remove(data.get(id).getEmail());
        data.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(data.values());
    }
}