package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDtoToCreate {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @Email(message = "Неправильный формат почты")
    private String email;

}
