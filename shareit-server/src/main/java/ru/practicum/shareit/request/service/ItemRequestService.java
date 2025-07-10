package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllItemsRequests();

    List<ItemRequestDto> getItemsRequestsForUser(Long userId);

    ItemRequestDto getItemRequest(Long requestId);

}
