package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private long id = 0L;

    @Override
    public Item addItem(Item item) {
        id += 1;

        item.setId(id);

        items.put(id, item);

        return item;
    }

    @Override
    public List<Item> getAllItems(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(long itemId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new NotFoundException("Вещи с ID: " + itemId + " не найдено");
        }

        return item;
    }

    @Override
    public List<Item> searchForItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(searchText) || description.contains(searchText);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Item editItem(Item itemToEdit, Item item) {
        if (item.getName() != null) {
            itemToEdit.setName(item.getName().trim());
        }
        if (item.getDescription() != null) {
            itemToEdit.setDescription(item.getDescription().trim());
        }
        if (item.getAvailable() != null) {
            itemToEdit.setAvailable(item.getAvailable());
        }

        items.put(itemToEdit.getId(), itemToEdit);

        return item;
    }
}
