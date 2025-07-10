package ru.practicum.server.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.mapper.ItemRequestMapper;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id: " + userId + " не найдено"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public List<ItemRequestDto> getAllItemsRequests() {
        List<ItemRequest> itemsRequests = itemRequestRepository.findAll();

        return itemsRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
                    return ItemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getItemsRequestsForUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id: " + userId + " не найдено"));

        List<ItemRequest> itemsRequests = itemRequestRepository.findAllByRequesterId(userId);

        return itemsRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
                    return ItemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequest(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id: " + requestId + " не найдено"));

        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());

        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

}
