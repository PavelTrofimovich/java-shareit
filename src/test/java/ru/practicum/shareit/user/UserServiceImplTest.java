package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUserTest() {
        User user = new User(1, "user", "user@mail.com");
        UserDto userDto = new UserDto(1, "user", "user@mail.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.addUser(userDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserTest() {
        User user = new User(1, "user", "user@mail.com");
        UserDto userDto = new UserDto(1, "user", "user@mail.com");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        UserDto result = userService.getUser(1);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(any(Integer.class));
    }

    @Test
    void updateUserTest() {
        User user = new User(1, "user", "user@mail.com");
        UserDto userDto = new UserDto(1, "user", "user@mail.com");
        User userUpdate = new User(1, "user", "update@mail.com");
        UserDto userDtoUpdate = new UserDto(1, "user", "update@mail.com");
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(userUpdate);
        UserDto result = userService.updateUser(userDto, user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, userDtoUpdate);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsersTest() {
        User user = new User(1, "user", "user@mail.com");
        UserDto userDto = new UserDto(1, "user", "user@mail.com");
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> result = userService.getAllUsers();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        User user2 = new User(2, "user2", "user2@mail.com");
        UserDto userDto2 = new UserDto(2, "user2", "user2@mail.com");
        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        List<UserDto> result2 = userService.getAllUsers();
        Assertions.assertNotNull(result2);
        Assertions.assertEquals(2, result2.size());
        Assertions.assertTrue(result2.contains(userDto));
        Assertions.assertTrue(result2.contains(userDto2));
        verify(userRepository, times(2)).findAll();
    }

    @Test
    void deleteUserTest() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
    }
}