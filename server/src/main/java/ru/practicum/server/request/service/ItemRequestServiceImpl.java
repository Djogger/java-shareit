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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = findUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public List<ItemRequestDto> getAllItemsRequests(Long userId) {
        List<ItemRequest> itemsRequests = itemRequestRepository.findAllExcept(userId);

        return getItemRequestsDtoWithItems(itemsRequests);
    }

    @Override
    public List<ItemRequestDto> getItemsRequestsForUser(Long userId) {
        findUserById(userId);

        List<ItemRequest> itemsRequests = itemRequestRepository.findAllByRequesterId(userId);

        return getItemRequestsDtoWithItems(itemsRequests);
    }

    @Override
    public ItemRequestDto getItemRequest(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id: " + requestId + " не найдено"));

        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());

        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    private List<ItemRequestDto> getItemRequestsDtoWithItems(List<ItemRequest> itemsRequests) {
        List<Item> items = itemRepository.findByRequestsIds(itemsRequests.stream().map(ItemRequest::getId).toList());

        Map<Long, List<Item>> requestItemsMap = itemsRequests.stream()
                .collect(Collectors.toMap(
                        ItemRequest::getId,
                        request -> items.stream()
                                .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(request.getId()))
                                .collect(Collectors.toList())
                ));

        return itemsRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, requestItemsMap.get(itemRequest.getId())))
                .toList();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id: " + userId + " не найдено"));
    }

}
