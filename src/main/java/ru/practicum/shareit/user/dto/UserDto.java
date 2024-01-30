package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    private String name;
    @NotBlank(groups = Create.class)
    @Email(groups = Create.class)
    @Size(max = 512)
    private String email;
}
