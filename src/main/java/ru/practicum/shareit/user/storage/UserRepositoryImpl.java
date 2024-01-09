package ru.practicum.shareit.user.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserStorage {
    private final Map<Long, User> users;
    private final Set<String> allEmails;
    private long id;

    public User create(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new ValidateException("Пользователь с таким email уже существует");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        allEmails.add(user.getEmail());
        return user;
    }

    @Override
    public boolean existsByEmail(String name) {
        return allEmails.contains(name);
    }

    @Override
    public User update(User user) {
        if (existsByEmail(user.getEmail())) {
            if (users.get(user.getId()).getEmail().contains(user.getEmail())) {
                return users.get(user.getId());
            }
            throw new RuntimeException();
        }
        findById(user.getId());
        User userUpdate;
        if (user.getEmail() == null) {
            userUpdate = users.computeIfPresent(user.getId(), (i, u) -> {
                u.setName(user.getName());
                return u;
            });
            return userUpdate;
        }
        if (user.getName() == null) {
            userUpdate = users.computeIfPresent(user.getId(), (i, u) -> {
                u.setEmail(user.getEmail());
                allEmails.remove(findById(user.getId()).getEmail());
                allEmails.add(user.getEmail());
                return u;
            });

        } else {
            userUpdate = users.computeIfPresent(user.getId(), (i, u) -> {
                allEmails.remove(findById(user.getId()).getEmail());
                allEmails.add(user.getEmail());
                u.setEmail(user.getEmail());
                u.setName(user.getName());
                return u;
            });
        }
        return userUpdate;
    }

    @Override
    public User findById(long userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException("User not found");
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
        allEmails.remove(findById(userId).getEmail());
        users.remove(userId);
    }

    private Long generateId() {
        return ++id;
    }

}
