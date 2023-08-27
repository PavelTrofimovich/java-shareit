package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemCommentDto getItem(Integer userId, Integer itemId);

    List<ItemCommentDto> getUserItems(Integer userId);

    List<ItemDto> searchItems(Integer userId, String search);

    CommentDto postComment(CommentDtoRequest commentDtoRequest, Integer itemId, Integer authorId);
}