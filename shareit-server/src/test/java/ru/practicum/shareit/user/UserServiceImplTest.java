package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceImplTest {
    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    void testSaveUser() {
        UserDto userDto = new UserDto(null, "Jack", "Jackson@mail.ru");

        userService.createUser(userDto);

        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u WHERE u.email = :email", User.class);

        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testSaveUserWithSameEmail() {
        UserDto userDto = new UserDto(null, "Jack", "Jackson@mail.ru");

        userService.createUser(userDto);

        UserDto sameUserDto = new UserDto(null, "Jacks", "Jackson@mail.ru");

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(sameUserDto));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto(null, "Jack", "Jackson@mail.ru");

        UserDto createdUser = userService.createUser(userDto);
        Long userId = createdUser.getId();

        UserDto userDtoToUpdate = new UserDto(userId, "JacksonShtorm", "JacksonShtorm@mail.ru");

        userService.updateUser(userDtoToUpdate, userDtoToUpdate.getId());

        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u WHERE u.id = :id", User.class);

        User user = query.setParameter("id", userId).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), not(userDto.getName()));
        assertThat(user.getEmail(), not(userDto.getEmail()));
    }

    @Test
    void testGetAllUsers() {
        assertThat(userService.getAllUsers(), equalTo(Collections.emptyList()));

        UserDto userDto1 = new UserDto(null, "User1", "user1@mail.ru");
        UserDto userDto2 = new UserDto(null, "User2", "user2@mail.ru");
        UserDto userDto3 = new UserDto(null, "User3", "user3@mail.ru");

        userService.createUser(userDto1);
        userService.createUser(userDto2);
        userService.createUser(userDto3);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size(), not(0));
        assertThat(users.size(), equalTo(3));
    }

    @Test
    void testDeleteUser() {
        UserDto userDto1 = new UserDto(null, "User1", "user1@mail.ru");
        UserDto userDto2 = new UserDto(null, "User2", "user2@mail.ru");

        UserDto firstUser = userService.createUser(userDto1);
        userService.createUser(userDto2);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size(), not(0));
        assertThat(users.size(), equalTo(2));

        userService.deleteUser(firstUser.getId());

        users = userService.getAllUsers();

        assertThat(users.size(), not(0));
        assertThat(users.size(), equalTo(1));
    }

}
