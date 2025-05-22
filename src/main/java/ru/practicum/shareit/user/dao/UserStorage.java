package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User userToUpdate, User user, long userId);

    User getUserById(long userId);

    List<User> getAllUsers();

    void deleteUser(long userId);

    User getUserEntity(long userId);
}
