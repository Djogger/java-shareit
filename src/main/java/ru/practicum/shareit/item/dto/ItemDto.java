package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    Long id;

    @NotNull
    @NotBlank(message = "Поле с названием не может быть пустым")
    String name;

    @NotNull
    @NotBlank(message = "Поле с описанием не может быть пустым")
    String description;

    @NotNull(message = "Поле со статусом не может быть пустым")
    Boolean available;
    UserDto owner;
    Long request;
}
