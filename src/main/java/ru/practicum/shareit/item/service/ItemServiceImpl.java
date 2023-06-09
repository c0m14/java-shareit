package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        if (itemDto.getId() != null) {
            throw new InvalidParamException(
                    "Item id",
                    "Id should not been sent in creation request"
            );
        }
        userService.getById(userId);

        Item item = itemMapper.mapToItem(itemDto);
        item.setOwnerId(userId);

        return itemMapper.mapToDto(itemRepository.add(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item itemToUpdate = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotExistsException(
                        "Item",
                        String.format("Item with id %d not exist", itemDto.getId())
                ));

        if (!Objects.equals(itemToUpdate.getOwnerId(), userId)) {
            throw new NotExistsException(
                    "Item",
                    String.format("Item with id %d not found for user with id %d", itemToUpdate.getId(), userId)
            );
        }

        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.mapToDto(itemRepository.update(itemToUpdate));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userService.getById(userId);
        Item requestedItem = itemRepository.getById(itemId).orElseThrow(
                () -> new NotExistsException(
                        "Item",
                        String.format("Item with id {} does not exist", itemId)
                )
        );
        return itemMapper.mapToDto(requestedItem);
    }

    @Override
    public List<ItemDto> getUsersItems(Long userId) {
        userService.getById(userId);
        return itemRepository.getUsersItems(userId).stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String requestedText) {
        if (requestedText.isBlank()) {
            return List.of();
        }
        userService.getById(userId);

        return itemRepository.searchItems(requestedText).stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());
    }

}
