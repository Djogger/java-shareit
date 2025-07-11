package ru.practicum.server.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;


    @Test
    void testBookingDtoSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(5);

        BookingDto bookingDto = new BookingDto(1L, start, end, new BookingDto.Item(1L, "PC"), 1L, new BookingDto.User(1L), BookingStatus.APPROVED);

        JsonContent<BookingDto> jsonContext = json.write(bookingDto);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        assertThat(jsonContext).hasJsonPath("$.id");
        assertThat(jsonContext).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(jsonContext).hasJsonPath("$.start");
        assertThat(jsonContext).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(formatter));
        assertThat(jsonContext).hasJsonPath("$.end");
        assertThat(jsonContext).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(formatter));
        assertThat(jsonContext).hasJsonPath("$.item");
        assertThat(jsonContext).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId().intValue()).isEqualTo(bookingDto.getItemId().intValue());
        assertThat(jsonContext).hasJsonPath("$.booker");
        assertThat(jsonContext).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(jsonContext).hasJsonPath("$.status");
        assertThat(jsonContext).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());

        BookingDto bookingDtoForTest = json.parseObject(jsonContext.getJson());

        assertThat(bookingDtoForTest).isEqualTo(bookingDto);
    }

}
