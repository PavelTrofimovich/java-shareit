package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemMapperTest {
    @Test
    void toItemTest() {
        ItemDto itemDto = new ItemDto(99, "name", "description", true, null);
        Item item = ItemMapper.toItem(itemDto);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toItemDtoTest() {
        User user = new User(1, "name", "mail@email.com");
        Item item = new Item(1, "name", "description", true, user, null);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemDto.getId(), item.getId());
        Assertions.assertEquals(itemDto.getName(), item.getName());
        Assertions.assertEquals(itemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), item.getAvailable());
        Assertions.assertNull(itemDto.getRequestId());
    }

    @Test
    void toItemCommentDtoTest() {
        User user = new User(1, "name", "mail@email.com");
        Item item = new Item(1, "name", "description", true, user, null);
        BookingDto lastBookingDto = new BookingDto(1, 3, 1);
        BookingDto nextBookingDto = new BookingDto(2, 4, 1);
        CommentDto commentDto = new CommentDto(1, "text", "name", LocalDateTime.now());
        List<CommentDto> comments = new ArrayList<>();
        comments.add(commentDto);
        ItemCommentDto itemCommentDto = ItemMapper.toItemCommentDto(item, comments, lastBookingDto, nextBookingDto);
        Assertions.assertNotNull(itemCommentDto);
        Assertions.assertEquals(itemCommentDto.getId(), item.getId());
        Assertions.assertEquals(itemCommentDto.getName(), item.getName());
        Assertions.assertEquals(itemCommentDto.getDescription(), item.getDescription());
        Assertions.assertEquals(itemCommentDto.getAvailable(), item.getAvailable());
        Assertions.assertNull(itemCommentDto.getRequestId());
        Assertions.assertEquals(itemCommentDto.getComments().size(), 1);
        Assertions.assertEquals(itemCommentDto.getComments().get(0), commentDto);
        Assertions.assertEquals(itemCommentDto.getLastBooking(), lastBookingDto);
        Assertions.assertEquals(itemCommentDto.getNextBooking(), nextBookingDto);
    }
}
