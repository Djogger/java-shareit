package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        return userStorage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        return userStorage.updateUser(userDto, userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public User getUserEntity(long userId) {
        return userStorage.getUserEntity(userId);
    }

}
