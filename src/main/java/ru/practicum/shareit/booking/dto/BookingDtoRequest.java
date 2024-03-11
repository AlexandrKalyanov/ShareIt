package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.customValidation.StartBeforeEndDateValid;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingDtoRequest {
    @NotNull
    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
