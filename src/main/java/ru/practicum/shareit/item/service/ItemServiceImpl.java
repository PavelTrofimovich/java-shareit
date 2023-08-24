package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UserNotHaveThisItem;
import ru.practicum.shareit.exception.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addNewItem(ItemDto itemDto, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new UserNotHaveThisItem();
        }
        Boolean available = itemDto.getAvailable();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        if (available != null) {
            item.setAvailable(available);
        }
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemCommentDto getItem(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        List<CommentDto> commentDto = new ArrayList<>();
        comments.forEach(comment -> commentDto.add(CommentMapper.toCommentDto(comment)));
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        if (item.getOwner().getId() == userId) {
            LocalDateTime dateTimeNow = LocalDateTime.now();
            lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(),
                            dateTimeNow, Status.APPROVED);
            nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(item.getId(),
                            dateTimeNow, Status.REJECTED);
        }
        return ItemMapper.toItemCommentDto(item, commentDto, lastBooking, nextBooking);
    }

    @Transactional
    @Override
    public List<ItemBookingDto> getUserItems(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        LocalDateTime dateTimeNow = LocalDateTime.now();
        List<Item> list = itemRepository.findAllByOwnerId(userId);
        List<ItemBookingDto> listItemBookingDto = new ArrayList<>();
        List<CommentDto> commentDto = new ArrayList<>();
        list.forEach(item -> {
            BookingDto lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(),
                            dateTimeNow, Status.APPROVED);
            BookingDto nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(item.getId(),
                            dateTimeNow, Status.REJECTED);
            List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
            listItemBookingDto.add(ItemMapper.toItemBookingDto(item, lastBooking, nextBooking));
        });
        return listItemBookingDto;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItems(Integer userId, String search) {
        if (search.isBlank()) {
            return new ArrayList<>();
        } else {
            List<ItemDto> itemDtoList = new ArrayList<>();
            itemRepository.findAllItemsByNameOrDescriptionContainingIgnoreCase(search)
                    .forEach(item -> itemDtoList.add(ItemMapper.toItemDto(item)));
            return itemDtoList;
        }
    }

    public CommentDto postComment(CommentDtoRequest commentDtoRequest, Integer itemId, Integer authorId) {
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(itemId, authorId, Status.APPROVED, dateTime);
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не может оставить коментарий");
        }
        Booking booking = bookings.get(0);
        Comment comment = CommentMapper.toComment(commentDtoRequest.getText(), booking.getBooker(), booking.getItem());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}