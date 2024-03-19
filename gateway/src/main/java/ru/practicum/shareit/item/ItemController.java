package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTOShort;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.valiadateGroup.Create;
import ru.practicum.shareit.item.dto.valiadateGroup.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.Collections;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;


@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("/items")
public class ItemController {


    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER) long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Income POST request DTO: {}, user ID: {}", itemRequestDto, userId);
        return itemClient.create(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER_USER) long userId,
                                         @Validated(Update.class) @RequestBody ItemRequestDto itemRequestDto,
                                         @PathVariable long itemId) {
        log.info("Income PATCH request DTO: {}, user ID: {}, item ID: {}", itemRequestDto, userId, itemId);
        return itemClient.update(userId, itemRequestDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findByItemId(@RequestHeader(HEADER_USER) long userId,
                                               @PathVariable long itemId) {
        log.info("Income GET request: user ID {}, item ID {}", userId, itemId);
        return itemClient.findByItemId(userId, itemId);

    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestHeader(HEADER_USER) long userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET request (find all by user): user ID {}", userId);
        return itemClient.findAllByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HEADER_USER) long userId,
                                              @RequestParam String text,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET request (search items): user id: {}, text: {}", userId, text);
        if (text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HEADER_USER) long userId,
                                                @PathVariable long itemId,
                                                @Valid @RequestBody CommentDTOShort commentDTOshort) {
        log.info("Income POST request to create comment user ID: {}, itemID: {}, Comment: {}", userId, itemId, commentDTOshort);
        return itemClient.createComment(userId, itemId, commentDTOshort);
    }


}
