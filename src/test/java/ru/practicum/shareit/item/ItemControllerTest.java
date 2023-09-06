package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto itemDto;
    private ItemCommentDto itemCommentDto;

    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = new ItemDto(1, "name", "description", true, null);
        itemCommentDto = new ItemCommentDto(itemDto, null, null, null);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void addNewItemTest() throws Exception {
        when(itemService.addNewItem(any(), anyInt())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), anyInt(), anyInt())).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyInt(), anyInt())).thenReturn(itemCommentDto);
        mvc.perform(get("/items/{itemId}", itemCommentDto.getId())
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemCommentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemCommentDto.getName())))
                .andExpect(jsonPath("$.description", is(itemCommentDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemCommentDto.getAvailable())));
    }

    @Test
    void getAllItemsOwnerTest() throws Exception {
        when(itemService.getUserItems(anyInt(), eq(0), eq(10))).thenReturn(List.of(itemCommentDto));
        mvc.perform(get("/items")
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void searchItemByTextTest() throws Exception {
        when(itemService.searchItems(anyInt(), anyString(), eq(0), eq(10))).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=text")
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "text", "author", LocalDateTime.now());
        when(itemService.postComment(any(), anyInt(), anyInt())).thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", commentDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .header(USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }
}