package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserRequestDtoToCreate;
import ru.practicum.user.dto.UserRequestDtoToUpdate;

@Controller
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserRequestDtoToCreate requestDto) {
        log.info("Adding a User: {}", requestDto);
        return userClient.createUser(requestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserRequestDtoToUpdate requestDto,
                                             @PathVariable Long userId) {
        log.info("Update a User: {}, {}", userId, requestDto);
        return userClient.updateUser(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Getting all users");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Getting a User with Id: {}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete a User: {}", userId);
        return userClient.deleteUser(userId);
    }

}
