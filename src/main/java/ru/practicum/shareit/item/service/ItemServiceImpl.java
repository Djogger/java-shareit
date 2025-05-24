package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.BookingItemMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) {
            throw new NullPointerException("Тело запроса пустое");
        }

        UserDto owner = userService.getUserById(ownerId);

        Item item = ItemMapper.toItem(itemDto, owner);

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
    public List<ItemDto> getAllItems(Long ownerId) {
        return itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId).stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
        Item itemToEdit = ItemMapper.toItem(ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id: " + itemId + " не найдено"))), userService.getUserById(ownerId));

        UserDto owner = userService.getUserById(ownerId);

        if (!itemToEdit.getOwner().getId().equals(ownerId) && owner != null) {
            throw new NotFoundException("Редактирование доступно только владельцу");
        }

        Item item = ItemMapper.toItem(itemDto, owner);

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
