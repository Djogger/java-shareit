package ru.practicum.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotNull
    @NotBlank(message = "Поле с описанием вещи не может быть пустым")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
