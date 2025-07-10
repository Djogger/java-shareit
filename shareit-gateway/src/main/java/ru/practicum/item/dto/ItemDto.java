package ru.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotNull
    @NotBlank(message = "Поле с названием не может быть пустым")
    private String name;

    @NotNull
    @NotBlank(message = "Поле с описанием не может быть пустым")
    private String description;

    @NotNull(message = "Поле со статусом не может быть пустым")
    private Boolean available;

    private Long requestId;

}
