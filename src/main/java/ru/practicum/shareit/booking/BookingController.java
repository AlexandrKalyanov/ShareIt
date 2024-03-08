package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDtoResponse create(@RequestHeader(HEADER_USER) long userId,
                                     @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("Income POST request to create booking DTO: {}, user ID: {}", bookingDtoRequest, userId);
        return this.bookingService.create(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@RequestHeader(HEADER_USER) long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        return this.bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse findById(@RequestHeader(HEADER_USER) long userId,
                                       @PathVariable() long bookingId) {
        log.info("Input GET bookingId: {}, userId: {}", bookingId, userId);
        return this.bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> findAllByBooker(@RequestHeader(HEADER_USER) long userId,
                                                          @RequestParam(defaultValue = "ALL") State state,
                                                          @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                                          @Valid @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET bookings userID:{} state:{},", userId, state);
        return this.bookingService.findAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> findAllForOwner(@RequestHeader(HEADER_USER) long ownerId,
                                                         @RequestParam(defaultValue = "ALL") State state,
                                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                                         @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET bookings ownerID:{} state:{},", ownerId, state);
        return bookingService.findAllForOwner(ownerId, state, from, size);
    }

}
