package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestOutcomeDto;

import javax.validation.Valid;
import java.util.Collection;


import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@Slf4j
@RequiredArgsConstructor
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
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10")  int size) {
        return requestService.findAll(userId,from,size);
    }
    @GetMapping("/{requestId}")
    public ItemRequestOutcomeDto findRequest(@RequestHeader(HEADER_USER) long userId,
                                             @PathVariable long requestId){
        return requestService.findRequest(userId,requestId);
    }
}
