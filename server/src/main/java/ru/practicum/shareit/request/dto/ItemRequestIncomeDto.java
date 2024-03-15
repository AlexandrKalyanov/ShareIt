package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;


@Value
@Jacksonized
@Builder
public class ItemRequestIncomeDto {
    String description;
}
