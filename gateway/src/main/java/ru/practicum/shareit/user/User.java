package ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String name;

    private String email;
}
