package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@SpringBootTest
@TestPropertySource(properties = {"db.name=testBooking"})
class BookingServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;
    private User booker;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        booker = User.builder().id(1).name("booker").email("booker@mail.com").build();
        User owner = User.builder().id(2).name("owner").email("owner2@mail.com").build();
        Item item = Item.builder().id(1).name("item").description("description").available(true).owner(owner).build();
        Booking bookingApprF = new Booking(1, now.plusDays(1), now.plusDays(2), item, booker, Status.APPROVED);
        Booking bookingApprP = new Booking(2, now.minusDays(1), now.minusDays(2), item, booker, Status.APPROVED);
        Booking bookingApprC = new Booking(3, now.minusDays(1), now.plusDays(2), item, booker, Status.APPROVED);
        Booking bookingRejF = new Booking(4, now.plusDays(1), now.plusDays(2), item, booker, Status.REJECTED);
        Booking bookingRejP = new Booking(5, now.minusDays(1), now.minusDays(2), item, booker, Status.REJECTED);
        Booking bookingRejC = new Booking(6, now.minusDays(1), now.plusDays(2), item, booker, Status.REJECTED);
        Booking bookingWaitF = new Booking(7, now.plusDays(1), now.plusDays(2), item, booker, Status.WAITING);
        Booking bookingWaitP = new Booking(8, now.minusDays(1), now.minusDays(2), item, booker, Status.WAITING);
        Booking bookingWaitC = new Booking(9, now.minusDays(1), now.plusDays(2), item, booker, Status.WAITING);
        userRepository.save(booker);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRepository.save(bookingApprF);
        bookingRepository.save(bookingApprP);
        bookingRepository.save(bookingApprC);
        bookingRepository.save(bookingRejF);
        bookingRepository.save(bookingRejP);
        bookingRepository.save(bookingRejC);
        bookingRepository.save(bookingWaitF);
        bookingRepository.save(bookingWaitP);
        bookingRepository.save(bookingWaitC);
    }

    @Test
    void getBookingsIntegrationTest() {
        Integer id = booker.getId();
        Assertions.assertEquals(9, bookingService.getBookings(id, "ALL", 0, 10).size());
        Assertions.assertEquals(3, bookingService.getBookings(id, "WAITING", 0, 10).size());
        Assertions.assertEquals(3, bookingService.getBookings(id, "APPROVED", 0, 10).size());
        Assertions.assertEquals(3, bookingService.getBookings(id, "REJECTED", 0, 10).size());
        Assertions.assertEquals(3, bookingService.getBookings(id, "CURRENT", 0, 10).size());
        Assertions.assertEquals(3, bookingService.getBookings(id, "FUTURE", 0, 10).size());
        Assertions.assertEquals(3, bookingService.getBookings(id, "PAST", 0, 10).size());

        Assertions.assertThrows(UnknownStateException.class,
                () -> bookingService.getBookings(id, "UnknownState", 0, 10).size());
    }
}