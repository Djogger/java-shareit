package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    List<Item> getAllItems(long ownerId);

    Item getItem(long itemId);

    List<Item> searchForItem(String text);

    Item editItem(Item itemToEdit, Item item);
}
