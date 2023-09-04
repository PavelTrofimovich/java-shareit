package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDtoResponse> jTesterBookingDtoResponse;

    @Autowired
    private JacksonTester<BookingDto> jTesterBookingDto;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

    @Test
    void bookingDtoResponseJsonTest() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        UserDto userDto = new UserDto(1, "name", "mail@mail.by");
        ItemDto itemDto = new ItemDto(1, "name", "description", true, 1);
        BookingDtoResponse dtoResponse = new BookingDtoResponse(1, start, end, Status.APPROVED, userDto, itemDto);

        JsonContent<BookingDtoResponse> content = jTesterBookingDtoResponse.write(dtoResponse);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void bookingDtoJsonTest() throws IOException {
        BookingDto dto = new BookingDto(1, 2, 3);
        JsonContent<BookingDto> content = jTesterBookingDto.write(dto);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(3);

    }
}