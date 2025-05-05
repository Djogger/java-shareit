package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    Long id;

    @NotBlank(message = "Поле с именем не может быть пустым")
    String name;

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Неправильный формат почты")
    String email;
}
