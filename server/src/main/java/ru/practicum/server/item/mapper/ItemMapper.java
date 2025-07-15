package ru.practicum.server.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.mapper.UserMapper;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        Long requestId;

        if (item.getRequest() == null) {
            requestId = null;
        } else {
            requestId = item.getRequest().getId();
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId
        );
    }

    public static Item toItem(ItemDto itemDto, UserDto owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(UserMapper.toUser(owner))
                .request(itemRequest)
                .build();
    }

}
