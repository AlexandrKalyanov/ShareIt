package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.BasicInfo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = BasicInfo.class)
    private String name;
    @NotBlank(groups = BasicInfo.class)
    @Email(groups = BasicInfo.class)
    private String email;
}
