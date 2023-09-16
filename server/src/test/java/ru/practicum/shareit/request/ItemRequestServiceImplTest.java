package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requester;
    private ItemRequest request;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private Item item;

    @BeforeEach
    void beforeEach() {
        requester = User.builder().id(1).name("requester").email("requester@mail.com").build();
        request = ItemRequest.builder().id(1).description("description").created(LocalDateTime.now())
                .requester(requester).build();
        ItemRequestDto itemRequestDto = new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated());
        User owner = User.builder().id(2).name("owner").email("owner@mail.com").build();
        item = Item.builder().id(1).name("item").description("description").available(true)
                .owner(owner).itemRequest(request).build();
        ItemDto itemDto = new ItemDto(1, "item", "description", true, request.getId());
        itemRequestDtoResponse = new ItemRequestDtoResponse(itemRequestDto, List.of(itemDto));
    }

    @Test
    void getUserRequestsTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(anyInt(), any(Sort.class))).thenReturn(List.of(request));
        when(itemRepository.findAllByItemRequestIn(List.of(request))).thenReturn(List.of(item));
        List<ItemRequestDtoResponse> result = itemRequestService.getUserRequests(requester.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDtoResponse, result.get(0));
    }

    @Test
    void getAllRequestsTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        PageRequest pageRequest = PageRequest.of(0, 5, DESC, "created");
        when(itemRequestRepository.findAllByRequesterIdNot(anyInt(), eq(pageRequest)))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(itemRepository.findAllByItemRequestIn(List.of(request))).thenReturn(List.of(item));
        List<ItemRequestDtoResponse> result = itemRequestService.getAllRequests(requester.getId(), 0, 5);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDtoResponse, result.get(0));
    }

    @Test
    void getRequestTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByItemRequestId(anyInt())).thenReturn(List.of(item));
        ItemRequestDtoResponse result = itemRequestService.getRequest(requester.getId(), request.getId());
        assertNotNull(result);
        assertEquals(itemRequestDtoResponse, result);
    }
}