package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Set<Item>> repository = new HashMap<>();
    private long idCounter = 1L;

    @Override
    public Item add(Item item) {
        item.setId(idCounter);
        idCounter++;
        repository.computeIfAbsent(item.getOwnerId(), key -> new HashSet<>()).add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        repository.get(item.getOwnerId()).remove(item);
        repository.get(item.getOwnerId()).add(item);
        return item;
    }

    @Override
    public Optional<Item> getById(Long id) {
        return repository.values().stream()
                .flatMap(Set::stream)
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst();
    }

    @Override
    public List<Item> getUsersItems(Long userId) {
        return new ArrayList<>(repository.get(userId));
    }

    @Override
    public List<Item> searchItems(String requestedText) {
        return repository.values().stream()
                .flatMap(Set::stream)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().trim().toLowerCase().contains(requestedText.toLowerCase()) ||
                        item.getDescription().trim().toLowerCase().contains(requestedText.toLowerCase()))
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());
    }
}
