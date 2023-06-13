package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Item update(Item item);

    Optional<Item> getById(Long id);

    List<Item> getUsersItems(Long userId);

    List<Item> searchItems(String requestedText);
}
