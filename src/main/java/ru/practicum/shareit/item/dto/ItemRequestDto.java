package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.valiadateGroup.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 512)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
}
