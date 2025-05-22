package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean isApproved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingByOwnerId(Long ownerId, String stringState);

    List<BookingDto> getAllBookingByUserId(Long userId, String stringState);
}
