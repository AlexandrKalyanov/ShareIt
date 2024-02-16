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
import ru.practicum.shareit.item.dto.valiadateGroup.Update;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;


@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
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
                                  @Validated(Update.class) @RequestBody ItemRequestDto itemRequestDto,
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
    public List<ItemResponseDto> findAllByUser(@RequestHeader(HEADER_USER) long userId,
                                               @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                               @Valid @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET request (find all by user): user ID {}", userId);
        return itemService.findAllByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader(HEADER_USER) long userId,
                                             @RequestParam String text,
                                             @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @Valid @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET request (search items): user id: {}, text: {}", userId, text);
        return itemService.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO createComment(@RequestHeader(HEADER_USER) long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDTOShort commentDTOshort) {
        log.info("Income POST request to create comment user ID: {}, itemID: {}, Comment: {}", userId, itemId, commentDTOshort);
        return itemService.createComment(userId, itemId, commentDTOshort);
    }


}
