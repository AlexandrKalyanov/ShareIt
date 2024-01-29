package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER = "X-Sharer-User-Id";


    @PostMapping
    public BookingDto create(@NotNull @RequestHeader(HEADER_USER) long userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("Income POST request to create booking DTO: {}, user ID: {}", bookingDto, userId);
        return this.bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@NotNull @RequestHeader(HEADER_USER) long userId,
                              @NotNull @PathVariable long bookingId,
                              @RequestParam boolean approved) {
        return this.bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@NotNull @RequestHeader(HEADER_USER) long userId,
                               @NotNull @PathVariable() long bookingId) {
        log.info("Input GET bookingId: {}, userId: {}", bookingId, userId);
        return this.bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findAllByBooker(@NotNull @RequestHeader(HEADER_USER) long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Income GET bookings userID:{} state:{},", userId, state);
        return this.bookingService.findAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Income GET bookings ownerID:{} state:{},", ownerId, state);
        return bookingService.findAllForOwner(ownerId, state);
    }

}
