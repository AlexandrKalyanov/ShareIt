package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestOutcomeDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestOutcomeDto create(@RequestHeader(HEADER_USER) long userId,
                                        @Valid @RequestBody ItemRequestIncomeDto itemRequestIncomeDto) {
        log.info("Income POST item request DTO: {}", itemRequestIncomeDto);
        return requestService.create(userId, itemRequestIncomeDto);
    }

    @GetMapping
    public Collection<ItemRequestOutcomeDto> findAllByUserId(@RequestHeader(HEADER_USER) long userId) {
        log.info("Income GET item request by User: {}", userId);
        return requestService.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestOutcomeDto> findAll(@RequestHeader(HEADER_USER) long userId,
                                                     @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @Valid @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET/all item request by User: {}", userId);
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutcomeDto findRequest(@RequestHeader(HEADER_USER) long userId,
                                             @PathVariable long requestId) {
        log.info("Income GET/{} item request by User: {}", requestId, userId);
        return requestService.findRequest(userId, requestId);
    }
}
