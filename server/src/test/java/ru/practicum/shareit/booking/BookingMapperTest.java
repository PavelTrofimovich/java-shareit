package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

class BookingMapperTest {

    @Test
    void testToBooking() {
        BookingDtoRequest dto = new BookingDtoRequest(1,
                LocalDateTime.of(2011, 1, 1, 1, 1, 1),
                LocalDateTime.of(2012, 1, 1, 1, 1, 1)
        );
        User user = new User(1, "name", "mail@email.com");
        Item item = new Item(1, "name", "description", true, user, null);
        Booking booking = BookingMapper.toBooking(dto, user, item);
        Assertions.assertNotNull(booking);
        Assertions.assertEquals(dto.getStart(), booking.getStart());
        Assertions.assertEquals(dto.getEnd(), booking.getEnd());
        Assertions.assertEquals(user, booking.getBooker());
        Assertions.assertEquals(item, booking.getItem());
        Assertions.assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void testToBookingDtoResponse() {
        Booking booking = new Booking();
        User user = new User(1, "name", "mail@email.com");
        Item item = new Item(1, "name", "description", true, user, null);
        booking.setId(1);
        booking.setStart(LocalDateTime.of(2011, 1, 1, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2012, 1, 1, 1, 1, 1));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        BookingDtoResponse dto = BookingMapper.toBookingDtoResponse(booking);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(booking.getId(), dto.getId());
        Assertions.assertEquals(booking.getStart(), dto.getStart());
        Assertions.assertEquals(booking.getEnd(), dto.getEnd());
        Assertions.assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        Assertions.assertEquals(booking.getItem().getId(), dto.getItem().getId());
        Assertions.assertEquals(booking.getStatus(), dto.getStatus());

    }

}