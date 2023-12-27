package ru.practicum.shareit.user.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users;
    private long id;

    public User create(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException();
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean existsByEmail(String name) {
        for (User u : users.values()) {
            if (u.getEmail().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public User update(long userId, User user) {
        if (existsByEmail(user.getEmail())) {
            if (users.get(userId).getEmail().contains(user.getEmail())) {
                return users.get(userId);
            }
            throw new RuntimeException();
        }
        if (!users.containsKey(userId)) {
            throw new RuntimeException();
        }
        User user1 = users.get(userId);
        if (user.getEmail() == null) {
            user1.setName(user.getName());
            users.put(userId, user1);
            return users.get(userId);
        }
        if (user.getName() == null) {
            user1.setEmail(user.getEmail());
            return users.get(userId);

        } else {
            user1.setEmail(user.getEmail());
            user1.setName(user.getName());
            users.put(userId, user1);
            return users.get(userId);
        }
    }

    @Override
    public User findById(long userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException("user not found");
        }
        return users.get(userId);
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(long userId) {
        if (!users.containsKey(userId)) {
            throw new RuntimeException();
        }
        users.remove(userId);
    }

    private Long generateId() {
        return ++id;
    }

}
