package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentDTOShort;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_USER = "X-Sharer-User-Id";


    @PostMapping
    public ItemCreateDto create(@NotNull @RequestHeader(HEADER_USER) long userId,
                                @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Income POST request DTO: {}, user ID: {}", itemCreateDto, userId);
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemUpdateDto update(@NotNull @RequestHeader(HEADER_USER) long userId,
                                @RequestBody ItemUpdateDto itemUpdateDto,
                                @PathVariable long itemId) {
        log.info("Income PATCH request DTO: {}, user ID: {}, item ID: {}", itemUpdateDto, userId, itemId);
        return itemService.update(userId, itemUpdateDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemCreateDto findByItemId(@NotNull @RequestHeader(HEADER_USER) long userId,
                                      @PathVariable long itemId) {
        log.info("Income GET request: user ID {}, item ID {}", userId, itemId);
        return itemService.findByItemId(userId, itemId);

    }

    @GetMapping
    public List<ItemCreateDto> findAllByUser(@NotNull @RequestHeader(HEADER_USER) long userId) {
        log.info("Income GET request (find all by user): user ID {}", userId);
        return itemService.findAllByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemUpdateDto> searchItems(@NotNull @RequestHeader(HEADER_USER) long userId,
                                           @NotNull @RequestParam String text) {
        log.info("Income GET request (search items): user id: {}, text: {}", userId, text);
        return itemService.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO createComment(@NotNull @RequestHeader(HEADER_USER) long userId,
                                    @NotNull @PathVariable long itemId,
                                    @Valid @RequestBody CommentDTOShort commentDTOshort) {
        log.info("Income POST request to create comment user ID: {}, itemID: {}, Comment: {}", userId, itemId, commentDTOshort);
        return itemService.createComment(userId, itemId, commentDTOshort);
    }


}
