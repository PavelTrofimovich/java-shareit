package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private static final String USER_ID = "X-Sharer-User-Id";
    private User owner;
    private User booker;
    private Booking booking;
    private BookingDtoResponse bookingDtoResponse;
    private BookingDtoRequest bookingDtoRequest;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        LocalDateTime now = LocalDateTime.now();
        owner = User.builder().id(1).name("owner").email("owner@mail.com").build();
        booker = User.builder().id(2).name("booker").email("booker@mail.com").build();
        Item item = Item.builder().id(1).name("item").description("description").available(true).owner(owner).build();
        booking = new Booking(1, now.plusDays(1), now.plusDays(2), item, booker, Status.APPROVED);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        UserDto userDto = UserMapper.toUserDto(booker);
        bookingDtoResponse = new BookingDtoResponse(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getStatus(), userDto, itemDto);
        bookingDtoRequest = new BookingDtoRequest(booking.getItem().getId(), booking.getStart(), booking.getEnd());
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(any(), anyInt())).thenReturn(bookingDtoResponse);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .header(USER_ID, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void approvedBookingTest() throws Exception {
        when(bookingService.approvedBooking(anyInt(), anyInt(), any(Boolean.class))).thenReturn(bookingDtoResponse);
        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .param("approved", "true")
                        .header(USER_ID, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookingsTest() throws Exception {
        when(bookingService.getBookings(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));
        mvc.perform(get("/bookings?state={state}", State.ALL)
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(bookingDtoResponse.getId())))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status",
                        containsInAnyOrder(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookingsOwnerTest() throws Exception {
        when(bookingService.getBookingsOwner(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));
        mvc.perform(get("/bookings/owner?state={state}", State.ALL)
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(bookingDtoResponse.getId())))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(bookingDtoResponse.getStatus().toString())));
    }
}