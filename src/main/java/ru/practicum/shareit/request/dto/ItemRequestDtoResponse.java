package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestDtoResponse extends ItemRequestDto {

    private List<ItemDto> items;

    public ItemRequestDtoResponse(ItemRequestDto itemRequestDto, List<ItemDto> items) {
        super(itemRequestDto.getId(), itemRequestDto.getDescription(), itemRequestDto.getCreated());
        this.items = items != null ? items : List.of();
    }
}