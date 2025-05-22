package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    String item;
    String booker;
    BookingStatus status;
}
