package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    private User owner;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(User.builder().name("name").email("mail@mail.com").build());
        User user = userRepository.save(User.builder().name("name2").email("mail2@mail.com").build());
        request = requestRepository.save(ItemRequest.builder().requester(user).created(LocalDateTime.now())
                .description("description").build());
        item = itemRepository.save(Item.builder().name("item").description("description")
                .available(true).itemRequest(request).owner(owner).build());
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Item> result = itemRepository.findAllByOwnerId(owner.getId(), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item, result.get(0));
    }

    @Test
    void findAllItemsByNameOrDescriptionContainingIgnoreCaseTest() {
        List<Item> result = itemRepository
                .findAllItemsByNameOrDescriptionContainingIgnoreCase("description", Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item, result.get(0));
    }

    @Test
    void findAllByItemRequestInTest() {
        List<Item> result = itemRepository.findAllByItemRequestIn(List.of(request));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item, result.get(0));
    }

    @Test
    void findAllByItemRequestId() {
        List<Item> result = itemRepository.findAllByItemRequestId(request.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item, result.get(0));
    }
}