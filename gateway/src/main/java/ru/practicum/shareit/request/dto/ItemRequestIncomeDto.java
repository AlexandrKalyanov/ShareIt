package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Jacksonized
@Builder
public class ItemRequestIncomeDto {
    @NotNull @Size(max = 512) String description;
}
