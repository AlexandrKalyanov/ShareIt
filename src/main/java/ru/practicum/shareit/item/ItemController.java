package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentDTOShort;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.valiadateGroup.Create;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    public ItemResponseDto create(@RequestHeader(HEADER_USER) long userId,
                                  @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Income POST request DTO: {}, user ID: {}", itemRequestDto, userId);
        return itemService.create(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader(HEADER_USER) long userId,
                                  @Valid @RequestBody ItemRequestDto itemRequestDto,
                                  @PathVariable long itemId) {
        log.info("Income PATCH request DTO: {}, user ID: {}, item ID: {}", itemRequestDto, userId, itemId);
        return itemService.update(userId, itemRequestDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findByItemId(@RequestHeader(HEADER_USER) long userId,
                                        @PathVariable long itemId) {
        log.info("Income GET request: user ID {}, item ID {}", userId, itemId);
        return itemService.findByItemId(userId, itemId);

    }

    @GetMapping
    public List<ItemResponseDto> findAllByUser(@RequestHeader(HEADER_USER) long userId) {
        log.info("Income GET request (find all by user): user ID {}", userId);
        return itemService.findAllByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader(HEADER_USER) long userId,
                                             @RequestParam String text) {
        log.info("Income GET request (search items): user id: {}, text: {}", userId, text);
        return itemService.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO createComment(@RequestHeader(HEADER_USER) long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDTOShort commentDTOshort) {
        log.info("Income POST request to create comment user ID: {}, itemID: {}, Comment: {}", userId, itemId, commentDTOshort);
        return itemService.createComment(userId, itemId, commentDTOshort);
    }


}
