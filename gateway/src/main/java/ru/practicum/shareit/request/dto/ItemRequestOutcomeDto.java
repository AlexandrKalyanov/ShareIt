package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestOutcomeDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User requester;
    private Collection<ItemForRequest> items;
}
