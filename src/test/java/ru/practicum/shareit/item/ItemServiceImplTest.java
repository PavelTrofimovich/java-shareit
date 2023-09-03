package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User requester;
    private ItemRequest request;
    private Item item;
    private ItemDto itemDto;
    private ItemCommentDto itemCommentDto;
    private Booking booking;
    private BookingDto lastBookingDto;
    private BookingDto nextBookingDto;
    private Comment comment;
    private CommentDto commentDto;

    private final PageRequest pageRequest = PageRequest.of(0, 5);

    @BeforeEach
    void beforeEach() {
        owner = User.builder().id(1).name("owner").email("owner@mail.com").build();
        requester = User.builder().id(3).name("requester").email("requestor@mail.com").build();
        request = ItemRequest.builder().id(1).requester(requester).description("description")
                .created(LocalDateTime.now()).build();
        item = Item.builder().id(1).name("item").description("description").available(true).owner(owner)
                .itemRequest(request).build();
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                request.getId());
        User commentator = User.builder().id(4).name("owner").email("owner@mail.com").build();
        booking = Booking.builder().id(1).item(item).booker(commentator).build();
        lastBookingDto = new BookingDto(booking.getId(), booking.getBooker().getId(), booking.getItem().getId());
        Booking nextBooking = Booking.builder().id(2).item(item).booker(commentator).build();
        nextBookingDto = new BookingDto(nextBooking.getId(), nextBooking.getBooker().getId(),
                nextBooking.getItem().getId());
        comment = new Comment(1, "text", item, commentator, LocalDateTime.now());
        commentDto = new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                comment.getCreated());
        itemCommentDto = new ItemCommentDto(itemDto, lastBookingDto, nextBookingDto, List.of(commentDto));
    }

    @Test
    void addItemTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.addNewItem(itemDto, 1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemDto, result);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemTest() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto result = itemService.updateItem(itemDto, item.getId(), owner.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, itemDto);
        verify(itemRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void getUserItemsTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findAllByOwnerId(anyInt(), eq(pageRequest))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findAllByItemInAndStartAfterAndStatusNot(eq(List.of(item)), any(LocalDateTime.class),
                eq(Status.REJECTED), any(Sort.class))).thenReturn(List.of(nextBookingDto));
        when(bookingRepository.findAllByItemInAndStartBeforeAndStatus(eq(List.of(item)), any(LocalDateTime.class),
                eq(Status.APPROVED), any(Sort.class))).thenReturn(List.of(lastBookingDto));
        when(commentRepository.findByItemIn(eq(List.of(item)), any(Sort.class))).thenReturn(List.of(comment));

        List<ItemCommentDto> result = itemService.getUserItems(owner.getId(), 0, 5);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(itemCommentDto, result.get(0));
    }

    @Test
    void searchItemTest() {
        when(itemRepository.findAllItemsByNameOrDescriptionContainingIgnoreCase(anyString(),
                eq(pageRequest))).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> result = itemService.searchItems(1, "text", 0, 5);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(itemDto, result.get(0));
        verify(itemRepository, times(1))
                .findAllItemsByNameOrDescriptionContainingIgnoreCase(anyString(), eq(pageRequest));
    }

    @Test
    void postCommentTest() {
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(anyInt(), anyInt(),
                eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto result = itemService.postComment(new CommentDtoRequest("text"), 1, 4);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(commentDto, result);
    }
}