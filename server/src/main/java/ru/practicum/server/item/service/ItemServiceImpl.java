package ru.practicum.server.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.dto.BookingItemDto;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.mapper.BookingItemMapper;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.repository.UserRepository;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) {
            throw new NullPointerException("Тело запроса пустое");
        }

        UserDto owner = userService.getUserById(ownerId);

        ItemRequest request = null;

        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запроса с id: " + itemDto.getRequestId() + " не найдено"));
        }

        Item item = ItemMapper.toItem(itemDto, owner, request);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationException("Комментарий не содержит текста");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещи с id: " + itemId + " не найдено"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователя с id: " + userId + " не найдено"));

        Comment comment = CommentMapper.toComment(commentDto, item, user);

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdStatePast(comment.getUser().getId(), itemId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь с id: " + userId + " ничего не бронировал");
        }

        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<BookingItemDto> getAllItems(Long ownerId) {
        List<ItemDto> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId).stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .toList();

        List<Long> ids = items.stream()
                .map(ItemDto::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, List<Booking>> bookings = bookingRepository.findByItemsIds(ids).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> comments = commentRepository.findByItemsIds(ids).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        log.info("ids: " + ids);
        log.info("bookings: " + bookings);
        log.info("comments: " + comments);

        return items.stream()
                .map(item -> BookingItemMapper.itemDtoToBookingItemDto(item, comments.getOrDefault(item.getId(), Collections.emptyList()),
                                bookings.getOrDefault(item.getId(), Collections.emptyList()).stream()
                                        .filter(b -> b.getEnd().isBefore(LocalDateTime.now())).max(Comparator.comparing(Booking::getEnd)).orElse(null),
                                bookings.getOrDefault(item.getId(), Collections.emptyList()).stream()
                                        .filter(b -> b.getStart().isAfter(LocalDateTime.now())).min(Comparator.comparing(Booking::getStart)).orElse(null)))
                .toList();
    }

    @Override
    public BookingItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id: " + itemId + " не найдено"));

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            Booking lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, localDateTime);
            Booking nextBooking = bookingRepository.findTopByItemIdAndStartAfterOrderByStartAsc(itemId, localDateTime);

            return BookingItemMapper.toBookingItemDto(item, comments, lastBooking, nextBooking);
        }

        return BookingItemMapper.toBookingItemDto(item, comments, null, null);
    }

    @Override
    public List<ItemDto> searchForItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findByQuery(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto editItem(Long itemId, ItemDto itemDto, Long ownerId) {
        ItemRequest request = null;

        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запроса с id: " + itemDto.getRequestId() + " не найдено"));
        }

        Item itemToEdit = ItemMapper.toItem(ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id: " + itemId + " не найдено"))), userService.getUserById(ownerId), request);

        UserDto owner = userService.getUserById(ownerId);

        if (!itemToEdit.getOwner().getId().equals(ownerId) && owner != null) {
            throw new NotFoundException("Редактирование доступно только владельцу");
        }

        Item item = ItemMapper.toItem(itemDto, owner, null);

        if (item.getName() != null) {
            itemToEdit.setName(item.getName().trim());
        }
        if (item.getDescription() != null) {
            itemToEdit.setDescription(item.getDescription().trim());
        }
        if (item.getAvailable() != null) {
            itemToEdit.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(itemToEdit));
    }

}
