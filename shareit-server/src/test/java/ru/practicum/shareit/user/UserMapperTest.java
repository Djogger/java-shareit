package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Jack", "Jacks@mail.ru");
    }

    @Test
    void toUserDtoFromUser() {
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

}
