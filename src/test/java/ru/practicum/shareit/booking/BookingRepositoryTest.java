package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User booker;
    private Booking booking;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void beforeEach() {
        booker = userRepository.save(User.builder().name("name").email("mail@mail.com").build());
        User owner = userRepository.save(User.builder().name("name2").email("mail2@mail.com").build());
        item = itemRepository.save(Item.builder().name("item_name").description("description").owner(owner).available(true).build());
        booking = bookingRepository.save(Booking.builder().booker(booker).start(now.plusDays(1)).end(now.plusDays(3)).status(Status.APPROVED).item(item).build());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.APPROVED, Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerId(booker.getId(), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(), now.plusDays(2), now.plusDays(2), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), now, Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), now.plusDays(4), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStatus(item.getOwner().getId(), Status.APPROVED, Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerId(item.getOwner().getId(), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(item.getOwner().getId(), now.plusDays(2), now.plusDays(2), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartAfter(item.getOwner().getId(), now, Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndEndBefore(item.getOwner().getId(),  now.plusDays(4), Pageable.unpaged()).toList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @Test
    void findAllByItemIdAndBookerIdAndStatusIsAndEndBeforeTest() {
        List<Booking> result = bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(item.getId(), booker.getId(), Status.APPROVED,  now.plusDays(4));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking, result.get(0));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}