package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JUnit 5 Nested")
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item;
    private Booking booking;

    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "Jack", "Jack@mail.ru");
        user2 = new User(2L, "Lena", "Lena@mail.ru");

        item = new Item(1L, "Telephone", "description", true, user1,
                null);

        booking = new Booking(1L, start, end, item, user1, BookingStatus.WAITING);
    }

    @Nested
    @DisplayName("Тестирование метода create")
    class Create {
        @Test
        void createShouldReturnBookingDto() {
            booking = new Booking(1L, start, end, item, user2, BookingStatus.WAITING);

            BookingDto bookingDto = BookingMapper.toBookingDto(booking);

            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user2));
            Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
            Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

            BookingDto result = bookingService.create(bookingDto, user2.getId());

            assertEquals(1L, result.getId());
            assertEquals(start, result.getStart());
            assertEquals(end, result.getEnd());
            assertEquals(BookingStatus.WAITING, result.getStatus());
            assertEquals(1L, result.getItem().getId());
            assertEquals("Telephone", result.getItem().getName());
            assertEquals(2L, result.getBooker().getId());
        }

        @Test
        void createShouldReturnValidationExceptionOnNotAvailable() {
            item = new Item(1L, "Telephone", "description", false, user1,
                    null);

            booking = new Booking(1L, start, end, item, user2, BookingStatus.WAITING);

            BookingDto bookingDto = BookingMapper.toBookingDto(booking);

            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user2));
            Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));

            assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, user2.getId()));
        }

        @Test
        void createShouldReturnValidationExceptionOnBadStartTime() {
            booking = new Booking(1L, start.plusMonths(12), end, item, user2, BookingStatus.WAITING);

            BookingDto bookingDto = BookingMapper.toBookingDto(booking);

            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user2));
            Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));

            assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, user2.getId()));
        }

        @Test
        void createShouldReturnValidationExceptionOnBadEndTime() {
            item = new Item(1L, "Telephone", "description", false, user1,
                    null);

            booking = new Booking(1L, start, end.minusMonths(12), item, user2, BookingStatus.WAITING);

            BookingDto bookingDto = BookingMapper.toBookingDto(booking);

            Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user2));
            Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));

            assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, user2.getId()));
        }

    }

    @Nested
    @DisplayName("Тестирование метода create")
    class Update {
        @Test
        void updateShouldReturnBookingDto() {
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));
            Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

            BookingDto result = bookingService.update(booking.getId(), user1.getId(), true);

            assertEquals(result.getId(), booking.getId());
        }

        @Test
        void updateShouldReturnValidationExceptionOnNotOwner() {
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));

            assertThrows(ValidationException.class, () -> bookingService.update(booking.getId(), user2.getId(), true));
        }

        @Test
        void updateShouldReturnValidationExceptionOnAlreadyApproved() {
            booking.setStatus(BookingStatus.APPROVED);

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));

            assertThrows(ValidationException.class, () -> bookingService.update(booking.getId(), user1.getId(), true));
        }

        @Test
        void updateShouldReturnValidationExceptionOnAlreadyRejected() {
            booking.setStatus(BookingStatus.REJECTED);

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(booking));

            assertThrows(ValidationException.class, () -> bookingService.update(booking.getId(), user1.getId(), false));
        }

    }

    @Nested
    @DisplayName("Тестирование метода getAllBookingByOwnerId")
    class GetAllBookingByOwnerId {
        @Test
        void getAllBookingByOwnerIdStateAll() {
            Mockito.when(bookingRepository.findOwnerAll(Mockito.anyLong())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.ALL.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByOwnerIdStateFuture() {
            Mockito.when(bookingRepository.findOwnerFuture(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.FUTURE.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByOwnerIdStateCurrent() {
            Mockito.when(bookingRepository.findOwnerCurrent(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.CURRENT.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByOwnerIdStateWaiting() {
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.WAITING.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByOwnerIdStatePast() {
            Mockito.when(bookingRepository.findOwnerPast(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.PAST.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByOwnerIdStateRejected() {
            Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.REJECTED.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByOwnerIdShouldReternNotFoundException() {
            Mockito.when(bookingRepository.findOwnerAll(Mockito.anyLong())).thenReturn(List.of());

            assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByOwnerId(user1.getId(), BookingState.ALL.toString()));
        }

    }

    @Nested
    @DisplayName("Тестирование метода getAllBookingByUserId")
    class GetAllBookingByUserId {
        @Test
        void getAllBookingByUserIdStateAll() {
            Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyLong())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByUserId(user1.getId(), BookingState.ALL.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByUserIdStateFuture() {
            Mockito.when(bookingRepository.findFuture(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByUserId(user1.getId(), BookingState.FUTURE.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByUserIdStateCurrent() {
            Mockito.when(bookingRepository.findByBookerIdStateCurrent(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByUserId(user1.getId(), BookingState.CURRENT.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByUserIdStateWaiting() {
            Mockito.when(bookingRepository.findByBookerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByUserId(user1.getId(), BookingState.WAITING.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByUserIdStatePast() {
            Mockito.when(bookingRepository.findByBookerIdStatePast(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByUserId(user1.getId(), BookingState.PAST.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByUserIdStateRejected() {
            Mockito.when(bookingRepository.findByBookerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(booking));

            List<BookingDto> result = bookingService.getAllBookingByUserId(user1.getId(), BookingState.REJECTED.toString());

            assertEquals(1, result.size());
        }

        @Test
        void getAllBookingByUserIdShouldReternNotFoundException() {
            Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyLong())).thenReturn(List.of());

            assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByUserId(user1.getId(), BookingState.ALL.toString()));
        }

    }

    @Nested
    @DisplayName("Тестирование метода getBookingById")
    class GetBookingById {
        @Test
        void shouldReturnBookingDto() {
            Long bookingId = 1L;
            Long userId = 1L;
            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            BookingDto result = bookingService.getBookingById(bookingId, userId);

            assertEquals(1L, result.getId());
            assertEquals(start, result.getStart());
            assertEquals(end, result.getEnd());
            assertEquals(BookingStatus.WAITING, result.getStatus());
            assertEquals(1L, result.getItem().getId());
            assertEquals("Telephone", result.getItem().getName());
            assertEquals(1L, result.getBooker().getId());
        }

        @Test
        void shouldReturnUserNotFoundException() {
            Long bookingId = 1L;
            Long userId = 999L;

            Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

            assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
        }
    }

    @Test
    void shouldReturnBookingNotFoundException() {
        Long bookingId = 999L;
        Long userId = 1L;

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

}
