package ru.practicum.server.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

}
