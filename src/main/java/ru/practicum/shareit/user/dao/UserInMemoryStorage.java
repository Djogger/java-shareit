package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

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
    public UserDto createUser(UserDto userDto) {
        if (emailExists(userDto.getEmail(), null)) {
            throw new DuplicateEmailException("Такой email уже используется");
        }

        id += 1;

        User user = UserMapper.toUser(userDto);
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }


        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!userDto.getEmail().equals(existingUser.getEmail())) {
                if (emailExists(userDto.getEmail(), userId)) {
                    throw new DuplicateEmailException("Такой email уже используется");
                }
                existingUser.setEmail(userDto.getEmail());
            }
        }

        return UserMapper.toUserDto(existingUser);
    }

    private boolean emailExists(String email, Long excludeUserId) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email) &&
                        (excludeUserId == null || !u.getId().equals(excludeUserId)));
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>(users.values()).stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
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
