package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto addRequest(ItemRequestDtoRequest itemRequestDto, Integer userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequestSaved =
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, requester));
        return ItemRequestMapper.toItemRequestDto(itemRequestSaved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoResponse> getUserRequests(Integer userId) {
        if (userRepository.existsById(userId)) {
            List<ItemRequest> requestList = itemRequestRepository
                    .findAllByRequesterId(userId, Sort.by(DESC, "created"));
            Map<Integer, List<ItemDto>> map = itemRepository.findAllByItemRequestIn(requestList)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(groupingBy(ItemDto::getRequestId, toList()));
            return requestList.stream().map(itemRequest -> ItemRequestMapper
                    .toItemRequestDtoResponse(itemRequest, map.get(itemRequest.getId()))).collect(toList());
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoResponse> getAllRequests(Integer userId, int from, int size) {
        if (userRepository.existsById(userId)) {
            PageRequest pageRequest = PageRequest.of(from / size, size, DESC, "created");
            Page<ItemRequest> page = itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest);
            Map<Integer, List<ItemDto>> map = itemRepository.findAllByItemRequestIn(page.toList())
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(groupingBy(ItemDto::getRequestId, toList()));
            return page.stream().map(itemRequest -> ItemRequestMapper
                    .toItemRequestDtoResponse(itemRequest, map.get(itemRequest.getId()))).collect(toList());
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoResponse getRequest(Integer userId, Integer requestId) {
        if (userRepository.existsById(userId)) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            return ItemRequestMapper.toItemRequestDtoResponse(itemRequest,
                    itemRepository.findAllByItemRequestId(requestId)
                            .stream()
                            .map(ItemMapper::toItemDto).collect(toList()));
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}