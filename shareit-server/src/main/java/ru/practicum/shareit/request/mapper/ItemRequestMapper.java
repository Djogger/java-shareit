package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemDto> itemsRequestsDtoList = null;

        if (items != null) {
            itemsRequestsDtoList = items.stream()
                    .map(item -> new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequest().getId()))
                    .toList();
        }

        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), itemsRequestsDtoList);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), user, itemRequestDto.getCreated());
    }

}
