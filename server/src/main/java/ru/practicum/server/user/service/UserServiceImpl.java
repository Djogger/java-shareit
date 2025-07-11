package ru.practicum.server.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.DuplicateEmailException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (!emailExists(userDto.getEmail(), userDto.getId())) {
            User user = UserMapper.toUser(userDto);
            return UserMapper.toUserDto(userRepository.save(user));
        } else  {
            throw new DuplicateEmailException("Такой email уже используется");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User userToUpdate = UserMapper.toUser(getUserById(userId));

        User user = UserMapper.toUser(userDto);

        if (user.getEmail() != null && !user.getEmail().equals(userToUpdate.getEmail())) {
            if (emailExists(user.getEmail(), userId)) {
                throw new DuplicateEmailException("Такой email уже используется");
            }
            userToUpdate.setEmail(user.getEmail());
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }

        return UserMapper.toUserDto(userRepository.save(userToUpdate));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id: " + userId + " не найдено")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean emailExists(String email, Long excludeUserId) {
        return userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email) &&
                        (excludeUserId == null || !u.getId().equals(excludeUserId)));
    }

}
