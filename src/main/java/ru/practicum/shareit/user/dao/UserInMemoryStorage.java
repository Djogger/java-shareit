package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0L;

    @Override
    public User createUser(User user) {
        id += 1;

        user.setId(id);
        users.put(user.getId(), user);

        log.info("Создан новый пользователь: {}", user);

        return user;
    }

    @Override
    public User updateUser(User userToUpdate, User user, long userId) {
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }

        users.put(userToUpdate.getId(), userToUpdate);

        return userToUpdate;
    }

    @Override
    public User getUserById(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values()).stream()
                .toList();
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
        log.info("Удален пользователь с ID: {}", userId);
    }

    @Override
    public User getUserEntity(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return user;
    }
}
