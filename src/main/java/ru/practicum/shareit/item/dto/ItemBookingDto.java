package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@NoArgsConstructor
public class ItemBookingDto extends ItemDto {
    private BookingDto lastBooking;
    private BookingDto nextBooking;

    public ItemBookingDto(ItemDto itemDto, BookingDto lastBooking, BookingDto nextBooking) {
        super(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}