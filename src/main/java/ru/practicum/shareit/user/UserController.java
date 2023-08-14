package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя {}", userDto);
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer id) {
        log.info("Запрос на обновление пользователя с ID {}", id);
        return userService.updateUser(userDto, id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@Valid @PathVariable Integer id) {
        log.info("Запрос на вывод пользователя с ID {}", id);
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @PathVariable Integer id) {
        log.info("Запрос на удаление пользователя с ID {}", id);
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на вывод спика пользователей");
        return userService.getAllUsers();
    }
}