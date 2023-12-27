package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private static final String headerUser = "X-Sharer-User-Id";


    @PostMapping
    public ItemCreateDto create(@NotNull @RequestHeader(headerUser) long userId,
                                @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Icome DTO: {}, user ID: {}",itemCreateDto,userId);
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemUpdateDto update(@NotNull @RequestHeader(headerUser) long userId,
                                @RequestBody ItemUpdateDto itemUpdateDto,
                                @PathVariable long itemId) {
        return itemService.update(userId, itemUpdateDto, itemId);
    }
    @GetMapping("/{itemId}")
    public ItemCreateDto findByItemId(@NotNull @RequestHeader(headerUser) long userId,
                                      @PathVariable long itemId){

        return itemService.findByItemId(userId,itemId);

    }

    @GetMapping
    public List<ItemCreateDto> findAllByUser (@NotNull @RequestHeader(headerUser) long userId){
        return itemService.findAllByUser(userId);
    }
    @GetMapping("/search")
    public List<ItemCreateDto> searchItems (@NotNull @RequestHeader(headerUser) long userId,
                                            @NotNull @RequestParam String text){
        log.info("Params: user id: {}, text: {}",userId,text);
        return itemService.searchItems(userId,text);
    }


}
