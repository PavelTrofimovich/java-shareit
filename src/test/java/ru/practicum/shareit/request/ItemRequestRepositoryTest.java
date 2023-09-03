package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private User requester;
    private ItemRequest request;
    private final Sort SORT = Sort.by(DESC, "created");

    @BeforeEach
    public void beforeEach() {
        LocalDateTime dateTime = LocalDateTime.now();
        user = userRepository.save(User.builder().name("user").email("user@mail.com").build());
        requester = userRepository.save(User.builder().name("requester").email("requester@mail.com").build());
        request = itemRequestRepository.save(ItemRequest.builder().requester(requester).description("description")
                .created(dateTime).build());
    }

    @Test
    public void findAllByRequesterIdTest() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterId(requester.getId(), SORT);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(request.getDescription(), result.get(0).getDescription());
        assertEquals(request.getRequester(), result.get(0).getRequester());
        assertEquals(request.getCreated(), result.get(0).getCreated());
    }

    @Test
    public void findAllByRequesterIdNotTest() {
        Page<ItemRequest> result = itemRequestRepository.findAllByRequesterIdNot(user.getId(), Pageable.unpaged());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(request.getDescription(), result.getContent().get(0).getDescription());
        assertEquals(request.getRequester(), result.getContent().get(0).getRequester());
        assertEquals(request.getCreated(), result.getContent().get(0).getCreated());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}