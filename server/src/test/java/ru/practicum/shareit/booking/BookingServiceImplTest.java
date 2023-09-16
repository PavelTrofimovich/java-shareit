package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UserNotHaveThisItem;
import ru.practicum.shareit.exception.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private Item item;
    private User owner;
    private User booker;
    private LocalDateTime now;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        owner = User.builder().id(1).name("owner").email("owner@mail.com").build();
        booker = User.builder().id(2).name("booker").email("booker@mail.com").build();
        item = Item.builder().id(1).name("item").description("description").available(true).owner(owner).build();
        booking = new Booking(1, now.plusDays(1), now.plusDays(2), item, booker, Status.APPROVED);
    }

    @Test
    void addBookingTest() {
        BookingDtoRequest dto = new BookingDtoRequest(1, now.plusDays(1), now.plusDays(2));
        item.setAvailable(false);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.addBooking(dto, booker.getId()));

        item.setOwner(booker);
        item.setAvailable(true);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Assertions.assertThrows(UserNotHaveThisItem.class, () -> bookingService.addBooking(dto, booker.getId()));

        item.setOwner(owner);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoResponse result = bookingService.addBooking(dto, booker.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getId(), result.getId());
        Assertions.assertEquals(booking.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(booking.getStart(), result.getStart());
        Assertions.assertEquals(booking.getEnd(), result.getEnd());
        Assertions.assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        Assertions.assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void approvedBookingTest() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThrows(UserNotHaveThisItem.class, () -> bookingService.approvedBooking(2, 1, true));

        booking.setBooker(booker);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThrows(ValidationException.class, () -> bookingService.approvedBooking(1, 1, true));

        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoResponse result = bookingService.approvedBooking(1, 1, true);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getId(), result.getId());
        Assertions.assertEquals(booking.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(booking.getStart(), result.getStart());
        Assertions.assertEquals(booking.getEnd(), result.getEnd());
        Assertions.assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        Assertions.assertEquals(Status.APPROVED, result.getStatus());

        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoResponse resultFalse = bookingService.approvedBooking(1, 1, false);
        Assertions.assertNotNull(resultFalse);
        Assertions.assertEquals(Status.REJECTED, resultFalse.getStatus());
    }

    @Test
    void getBookingTest() {
        when(userRepository.existsById(anyInt())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 1));

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(3, 1));

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        BookingDtoResponse result = bookingService.getBooking(1, 1);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getId(), result.getId());
        Assertions.assertEquals(booking.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(booking.getStart(), result.getStart());
        Assertions.assertEquals(booking.getEnd(), result.getEnd());
        Assertions.assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        Assertions.assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void getBookingsTest() {
        when(userRepository.existsById(anyInt())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookings(1, "APPROVED", 1, 1));

        when(userRepository.existsById(anyInt())).thenReturn(true);
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(DESC, "start"));
        Page<Booking> page = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAllByBookerIdAndStatus(1, Status.WAITING, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result1 = bookingService.getBookings(1, "WAITING", 0, 5);
        Assertions.assertNotNull(result1);
        Assertions.assertEquals(1, result1.size());
        Assertions.assertEquals(booking.getId(), result1.get(0).getId());

        when(bookingRepository.findAllByBookerIdAndStatus(1, Status.APPROVED, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result2 = bookingService.getBookings(1, "APPROVED", 0, 5);
        Assertions.assertNotNull(result2);
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(booking.getId(), result2.get(0).getId());

        when(bookingRepository.findAllByBookerIdAndStatus(1, Status.REJECTED, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result3 = bookingService.getBookings(1, "REJECTED", 0, 5);
        Assertions.assertNotNull(result3);
        Assertions.assertEquals(1, result3.size());
        Assertions.assertEquals(booking.getId(), result3.get(0).getId());

        when(bookingRepository.findAllByBookerId(1, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result4 = bookingService.getBookings(1, "ALL", 0, 5);
        Assertions.assertNotNull(result4);
        Assertions.assertEquals(1, result4.size());
        Assertions.assertEquals(booking.getId(), result4.get(0).getId());

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(eq(1), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        List<BookingDtoResponse> result5 = bookingService.getBookings(1, "CURRENT", 0, 5);
        Assertions.assertNotNull(result5);
        Assertions.assertEquals(1, result5.size());
        Assertions.assertEquals(booking.getId(), result5.get(0).getId());

        when(bookingRepository.findAllByBookerIdAndStartAfter(eq(1), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        List<BookingDtoResponse> result6 = bookingService.getBookings(1, "FUTURE", 0, 5);
        Assertions.assertNotNull(result6);
        Assertions.assertEquals(1, result6.size());
        Assertions.assertEquals(booking.getId(), result6.get(0).getId());

        when(bookingRepository.findAllByBookerIdAndEndBefore(eq(1), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        List<BookingDtoResponse> result7 = bookingService.getBookings(1, "PAST", 0, 5);
        Assertions.assertNotNull(result7);
        Assertions.assertEquals(1, result7.size());
        Assertions.assertEquals(booking.getId(), result7.get(0).getId());
    }

    @Test
    void getBookingsOwnerTest() {
        when(userRepository.existsById(anyInt())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsOwner(1, "APPROVED", 1, 1));

        when(userRepository.existsById(anyInt())).thenReturn(true);
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(DESC, "start"));
        Page<Booking> page = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAllByItemOwnerIdAndStatus(1, Status.WAITING, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result1 = bookingService.getBookingsOwner(1, "WAITING", 0, 5);
        Assertions.assertNotNull(result1);
        Assertions.assertEquals(1, result1.size());
        Assertions.assertEquals(booking.getId(), result1.get(0).getId());

        when(bookingRepository.findAllByItemOwnerIdAndStatus(1, Status.APPROVED, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result2 = bookingService.getBookingsOwner(1, "APPROVED", 0, 5);
        Assertions.assertNotNull(result2);
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(booking.getId(), result2.get(0).getId());

        when(bookingRepository.findAllByItemOwnerIdAndStatus(1, Status.REJECTED, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result3 = bookingService.getBookingsOwner(1, "REJECTED", 0, 5);
        Assertions.assertNotNull(result3);
        Assertions.assertEquals(1, result3.size());
        Assertions.assertEquals(booking.getId(), result3.get(0).getId());

        when(bookingRepository.findAllByItemOwnerId(1, pageRequest)).thenReturn(page);
        List<BookingDtoResponse> result4 = bookingService.getBookingsOwner(1, "ALL", 0, 5);
        Assertions.assertNotNull(result4);
        Assertions.assertEquals(1, result4.size());
        Assertions.assertEquals(booking.getId(), result4.get(0).getId());

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(eq(1), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        List<BookingDtoResponse> result5 = bookingService.getBookingsOwner(1, "CURRENT", 0, 5);
        Assertions.assertNotNull(result5);
        Assertions.assertEquals(1, result5.size());
        Assertions.assertEquals(booking.getId(), result5.get(0).getId());

        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(eq(1), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        List<BookingDtoResponse> result6 = bookingService.getBookingsOwner(1, "FUTURE", 0, 5);
        Assertions.assertNotNull(result6);
        Assertions.assertEquals(1, result6.size());
        Assertions.assertEquals(booking.getId(), result6.get(0).getId());

        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(eq(1), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        List<BookingDtoResponse> result7 = bookingService.getBookingsOwner(1, "PAST", 0, 5);
        Assertions.assertNotNull(result7);
        Assertions.assertEquals(1, result7.size());
        Assertions.assertEquals(booking.getId(), result7.get(0).getId());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}