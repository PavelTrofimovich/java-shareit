package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCommentDto extends ItemBookingDto {
    private List<CommentDto> comments;

    public ItemCommentDto(ItemBookingDto itemBookingDto, List<CommentDto> comments) {
        super(itemBookingDto, itemBookingDto.getLastBooking(), itemBookingDto.getNextBooking());
        this.comments = comments;
    }
}