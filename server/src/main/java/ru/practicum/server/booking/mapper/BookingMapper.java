package ru.practicum.server.booking.mapper;

import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.Item item = new BookingDto.Item();
        BookingDto.User booker = new BookingDto.User();

        if (booking.getItem() != null) {
            item.setId(booking.getItem().getId());
            item.setName(booking.getItem().getName());
        }

        if (booking.getBooker() != null) {
            booker.setId(booking.getBooker().getId());
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(item)
                .itemId(booking.getItem().getId())
                .booker(booker)
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }

}
