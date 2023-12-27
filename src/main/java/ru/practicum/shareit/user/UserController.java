package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable long userId,
                       @RequestBody User user) {
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable long userId) {
        return userService.findById(userId);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        userService.deleteById(userId);
    }

}
