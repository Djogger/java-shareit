package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (emailExists(userDto.getEmail(), null)) {
            throw new DuplicateEmailException("Такой email уже используется");
        }

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User userToUpdate = userStorage.getUserById(userId);

        User user = UserMapper.toUser(userDto);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.getEmail().equals(userToUpdate.getEmail())) {
                if (emailExists(user.getEmail(), userId)) {
                    throw new DuplicateEmailException("Такой email уже используется");
                }
                userToUpdate.setEmail(user.getEmail());
            }
        }

        return UserMapper.toUserDto(userStorage.updateUser(userToUpdate, user, userId));
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.getUserEntity(userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public User getUserEntity(long userId) {
        return userStorage.getUserEntity(userId);
    }

    private boolean emailExists(String email, Long excludeUserId) {
        return userStorage.getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equals(email) &&
                        (excludeUserId == null || !u.getId().equals(excludeUserId)));
    }

}
