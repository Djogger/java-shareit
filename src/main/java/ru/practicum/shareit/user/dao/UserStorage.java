package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();

    void deleteUser(long userId);

    User getUserEntity(long userId);
}
