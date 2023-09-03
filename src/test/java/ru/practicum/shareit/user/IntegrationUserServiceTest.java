package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

@Transactional
@SpringBootTest
@TestPropertySource(properties = {"db.name=testBooking"})
public class IntegrationUserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsersTest() {
        Assertions.assertTrue(userService.getAllUsers().isEmpty());

        User user1 = new User(1, "user1", "user1@mail.com");
        User user2 = new User(2, "user2", "user2@mail.com");
        userRepository.save(user1);
        userRepository.save(user2);
        Assertions.assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void deleteUserTest() {
        Assertions.assertTrue(userRepository.findAll().isEmpty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUser(99));

        User user = userRepository.save(new User(1, "user", "user@mail.com"));
        Assertions.assertTrue(userRepository.existsById(user.getId()));

        userService.deleteUser(user.getId());
        Assertions.assertFalse(userRepository.existsById(user.getId()));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }
}