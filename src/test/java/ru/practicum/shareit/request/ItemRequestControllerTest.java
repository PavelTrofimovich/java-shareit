package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemRequest request;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;

    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        User requester = User.builder().id(1).name("requester").email("requester@mail.com").build();
        request = ItemRequest.builder().id(1).requester(requester).description("description")
                .created(LocalDateTime.now()).build();
        itemRequestDto = new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated());
        ItemDto itemDto = new ItemDto(1, "item", "description", true, request.getId());
        itemRequestDtoResponse = new ItemRequestDtoResponse(itemRequestDto, List.of(itemDto));
    }

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestService.addRequest(any(), anyInt())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void getUserRequestsTest() throws Exception {
        when(itemRequestService.getUserRequests(anyInt())).thenReturn(List.of(itemRequestDtoResponse));
        mvc.perform(get("/requests")
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(itemRequestDtoResponse.getId())))
                .andExpect(jsonPath("$[*].description",
                        containsInAnyOrder(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[*].created", containsInAnyOrder(notNullValue())));
    }

    @Test
    void getRequestTest() throws Exception {
        when(itemRequestService.getRequest(anyInt(), anyInt())).thenReturn(itemRequestDtoResponse);
        mvc.perform(get("/requests/{requestId}", request.getId())
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void getAllTest() throws Exception {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoResponse));
        mvc.perform(get("/requests/all")
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(itemRequestDtoResponse.getId())))
                .andExpect(jsonPath("$[*].description",
                        containsInAnyOrder(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[*].created", containsInAnyOrder(notNullValue())));
    }
}