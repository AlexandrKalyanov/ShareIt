package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findAllByBooker(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllForOwner(long ownerId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> approve(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved);
        return patch("/{bookingId}" + bookingId + "?approved={approved}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookingDtoRequest requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> create(UserDto user) {
        return post("", user);
    }

    public ResponseEntity<Object> findById(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }

    public ResponseEntity<Object> deleteById(long userId) {
        return delete("/" + userId);
    }

    public ResponseEntity<Object> update(long userId, UserDto userDto) {
        return patch("/" + userId, userDto);
    }
}
