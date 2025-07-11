package ru.practicum.gateway.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDtoToUpdate {
    private Long id;

    private String name;

    @Email(message = "Неправильный формат почты")
    private String email;

}
