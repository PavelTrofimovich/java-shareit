package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {
    @NotNull
    private Integer itemId;

    @Future
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    @AssertTrue
    private boolean isStartBeforeEnd() {  //Без этого метода валятся тесты
        return Objects.nonNull(start) && Objects.nonNull(end) && start.isBefore(end);
    }
}