package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Create user: {}", user);
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable long userId, @RequestBody User user) {
        log.info("Update user id: {}, user: {} ", userId, user);
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable long userId) {
        log.info("Find user: {}", userId);
        return userService.findById(userId);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Find all users");
        return userService.findAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        log.info("Delete user {}", userId);
        userService.deleteById(userId);
    }

}
