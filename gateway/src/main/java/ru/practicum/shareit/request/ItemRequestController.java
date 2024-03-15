package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER) long userId,
                                         @Valid @RequestBody ItemRequestIncomeDto itemRequestIncomeDto) {
        log.info("Income POST item request DTO: {}", itemRequestIncomeDto);

        return itemRequestClient.create(userId, itemRequestIncomeDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(HEADER_USER) long userId) {
        log.info("Income GET item request by User: {}", userId);
        return itemRequestClient.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(HEADER_USER) long userId,
                                          @RequestParam(defaultValue = "0") @Valid @Min(0) int from,
                                          @RequestParam(defaultValue = "10") @Valid @Min(0) int size) {
        log.info("Income GET/all item request by User: {}", userId);
        return itemRequestClient.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequest(@RequestHeader(HEADER_USER) long userId,
                                              @PathVariable long requestId) {
        log.info("Income GET/{} item request by User: {}", requestId, userId);
        return itemRequestClient.findRequest(userId, requestId);
    }
}
