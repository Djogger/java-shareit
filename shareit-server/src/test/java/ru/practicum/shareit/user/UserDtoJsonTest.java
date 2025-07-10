package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDtoSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "Jack", "jack@mail.ru");

        JsonContent<UserDto> jsonContext = json.write(userDto);

        assertThat(jsonContext).hasJsonPath("$.id");
        assertThat(jsonContext).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(jsonContext).hasJsonPath("$.name");
        assertThat(jsonContext).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(jsonContext).hasJsonPath("$.email");
        assertThat(jsonContext).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());

        UserDto userDtoForTest = json.parseObject(jsonContext.getJson());

        assertThat(userDtoForTest).isEqualTo(userDto);
    }
}
