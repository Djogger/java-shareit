package ru.practicum.server.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.booking.enums.BookingState;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        User booker = getBooker(userId);
        Item item = getItem(bookingDto.getItemId());

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean isApproved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь с id: " + userId + " не является владельцем");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && isApproved) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (booking.getStatus().equals(BookingStatus.REJECTED) && !isApproved) {
            throw new ValidationException("Бронирование уже отклонено");
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id: " + userId + " не найдено"));

        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        }

        throw new NotFoundException("Вы не можете получить информацию о данном бронировании с id: " + bookingId);
    }

    @Override
    public List<BookingDto> getAllBookingByOwnerId(Long ownerId, String stringState) {
        List<Booking> bookings = new ArrayList<>();

        BookingState state = getState(stringState);
        LocalDateTime localDateTime = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findOwnerAll(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findOwnerFuture(ownerId, localDateTime);
                break;
            case CURRENT:
                bookings = bookingRepository.findOwnerCurrent(ownerId, localDateTime);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case PAST:
                bookings = bookingRepository.findOwnerPast(ownerId, localDateTime);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
        }

        if (bookings.isEmpty()) {
            throw new NotFoundException("Список бронирования пользователя с id: " + ownerId + " - пуст");
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Long userId, String stringState) {
        List<Booking> bookings = new ArrayList<>();

        BookingState state = getState(stringState);
        LocalDateTime localDateTime = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdStatePast(userId, localDateTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdStateCurrent(userId, localDateTime);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            case FUTURE:
                bookings = bookingRepository.findFuture(userId, localDateTime);
                break;
        }

        if (bookings.isEmpty()) {
            throw new NotFoundException("Список бронирования пользователя с id: " + userId + " - пуст");
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User getBooker(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id: " + userId + " не найдено"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id: " + itemId + " не найдено"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id: " + bookingId + " не найдено"));
    }

    private static BookingState getState(String stringState) {
        BookingState state;

        if (stringState == null) {
            state = BookingState.ALL;
        } else {
            try {
                state = BookingState.valueOf(stringState);
            } catch (Exception e) {
                throw new ValidationException("Неизвестное состояние: " + stringState);
            }
        }

        return state;
    }

}
