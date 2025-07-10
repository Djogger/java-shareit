package ru.practicum.server.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    Long id;

    String name;

    String email;
}
