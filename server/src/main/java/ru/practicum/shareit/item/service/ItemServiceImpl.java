package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addNewItem(ItemDto itemDto, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = null;
        Integer itemRequestId = itemDto.getRequestId();
        if (itemRequestId != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item.setItemRequest(itemRequest);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new UserNotHaveThisItem("Пользователь не является владельцем вещи");
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
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime dateTimeNow = LocalDateTime.now();
            lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatus(item.getId(),
                            dateTimeNow, Status.APPROVED, Sort.by(DESC, "start"));
            nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusNot(item.getId(), dateTimeNow, Status.REJECTED,
                            Sort.by(ASC, "start"));
        }
        return ItemMapper.toItemCommentDto(item, commentDto, lastBooking, nextBooking);
    }

    @Transactional
    @Override
    public List<ItemCommentDto> getUserItems(Integer userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        LocalDateTime now = LocalDateTime.now();
        PageRequest page = PageRequest.of(from / size, size, Sort.by(ASC, "id"));
        Page<Item> items = itemRepository.findAllByOwnerId(userId, page);
        List<ItemCommentDto> listItemCommentDto = new ArrayList<>();
        List<BookingDto> nextBookingsList = bookingRepository
                .findAllByItemInAndStartAfterAndStatusNot(items.toList(), now, Status.REJECTED,
                        Sort.by(ASC, "start"));
        Map<Integer, List<BookingDto>> nextBookingsMap = nextBookingsList.stream()
                .collect(groupingBy(BookingDto::getItemId, toList()));
        List<BookingDto> lastBookingsList = bookingRepository
                .findAllByItemInAndStartBeforeAndStatus(items.toList(), now, Status.APPROVED,
                        Sort.by(DESC, "start"));
        Map<Integer, List<BookingDto>> lastBookingsMap = lastBookingsList.stream()
                .collect(groupingBy(BookingDto::getItemId, toList()));
        Map<Integer, List<Comment>> comments = commentRepository.findByItemIn(items.toList(),
                        Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), toList()));
        items.forEach(item -> {
            Integer itemId = item.getId();
            BookingDto nextBooking = null;
            BookingDto lastBooking = null;
            List<CommentDto> commentsList = null;
            if (nextBookingsMap.containsKey(itemId)) {
                nextBooking = nextBookingsMap.get(itemId).get(0);
            }
            if (lastBookingsMap.containsKey(itemId)) {
                lastBooking = lastBookingsMap.get(itemId).get(0);
            }
            if (comments.containsKey(itemId)) {
                commentsList = comments.get(itemId).stream().map(CommentMapper::toCommentDto).collect(toList());
            }
            listItemCommentDto.add(ItemMapper.toItemCommentDto(item, commentsList, lastBooking, nextBooking));
        });
        return listItemCommentDto;
    }

    @Transactional
    @Override
    public List<ItemDto> searchItems(Integer userId, String search, int from, int size) {
        if (search.isBlank()) {
            return new ArrayList<>();
        } else {
            PageRequest page = PageRequest.of(from / size, size);
            List<ItemDto> itemDtoList = new ArrayList<>();
            itemRepository.findAllItemsByNameOrDescriptionContainingIgnoreCase(search, page)
                    .forEach(item -> itemDtoList.add(ItemMapper.toItemDto(item)));
            return itemDtoList;
        }
    }

    @Override
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