package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private MockMvc mvc;

    private final LocalDateTime start = LocalDateTime.now().plusDays(5);
    private final LocalDateTime end = start.plusDays(21);

    @Test
    void createBookingShouldReturnBadRequest() throws Exception {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, start, end, null, null, null, BookingStatus.WAITING);

        Mockito.when(bookingService.create(Mockito.any(), Mockito.anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void update() throws Exception {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(bookingId, start, end, null, null, null, BookingStatus.WAITING);

        Mockito.when(bookingService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(bookingDto);

        String result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .param("bookingId", "1L"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

    @Test
    void getById() throws Exception {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(1L, start, end, null, null, null, BookingStatus.WAITING);

        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        String result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

    @Test
    void getAllByBooker() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, start, end, null, null, null, BookingStatus.WAITING);

        Mockito.when(bookingService.getAllBookingByUserId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(bookingDto));

        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @Test
    void getAllByOwner() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, start, end, null, null, null, BookingStatus.WAITING);

        Mockito.when(bookingService.getAllBookingByOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(bookingDto));

        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDto)), result);
    }

}
