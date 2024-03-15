package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.GlobalConst.HEADER_USER;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER) long userId,
                                         @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("Income POST request to create booking DTO: {}, user ID: {}", bookingDtoRequest, userId);
        return bookingClient.bookItem(userId, bookingDtoRequest);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(HEADER_USER) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER_USER) long userId,
                                           @PathVariable long bookingId) {
        log.info("Input GET bookingId: {}, userId: {}", bookingId, userId);
        return this.bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader(HEADER_USER) long userId,
                                                  @RequestParam(defaultValue = "ALL") State state,
                                                  @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @Valid @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET bookings userID:{} state:{},", userId, state);
        return this.bookingClient.findAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllForOwner(@RequestHeader(HEADER_USER) long ownerId,
                                                  @RequestParam(defaultValue = "ALL") State state,
                                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "10") @Min(0) int size) {
        log.info("Income GET bookings ownerID:{} state:{},", ownerId, state);
        return bookingClient.findAllForOwner(ownerId, state, from, size);
    }

}
