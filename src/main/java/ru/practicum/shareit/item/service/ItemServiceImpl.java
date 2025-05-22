package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        if (itemDto == null) {
            throw new NullPointerException("Тело запроса пустое");
        }

        UserDto owner = userService.getUserById(ownerId);

        Item item = ItemMapper.toItem(itemDto, owner);

        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        return itemStorage.getAllItems(ownerId).stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> searchForItem(String text) {
        return itemStorage.searchForItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto, long ownerId) {
        Item itemToEdit = itemStorage.getItem(itemId);

        if (!itemToEdit.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Редактирование доступно только владельцу");
        }

        UserDto owner = userService.getUserById(ownerId);

        return ItemMapper.toItemDto(itemStorage.editItem(itemToEdit, ItemMapper.toItem(itemDto, owner)));
    }

}
