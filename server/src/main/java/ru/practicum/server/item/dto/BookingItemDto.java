package ru.practicum.server.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.model.Booking;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingItemDto {
    private Long id;

    @NotNull
    @NotBlank(message = "Поле с названием не может быть пустым")
    private String name;

    @NotNull
    @NotBlank(message = "Поле с описанием не может быть пустым")
    private String description;

    @NotNull(message = "Поле со статусом не может быть пустым")
    private Boolean available;

    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;

}