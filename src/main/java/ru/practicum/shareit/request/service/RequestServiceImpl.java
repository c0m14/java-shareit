package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreationRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestNoItemsDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper requestMapper;
    private final ItemMapper itemMapper;
    private final Sort sortByCreatedDesc = Sort.by(Sort.Direction.DESC, "created");

    @Override
    @Transactional
    public RequestNoItemsDto add(Long userId, CreationRequestDto creationRequestDto) {
        User owner = getUserById(userId);
        Request request = requestMapper.mapToEntity(creationRequestDto, owner);

        return requestMapper.mapToNoItemsDto(
                requestRepository.save(request)
        );
    }

    @Override
    @Transactional
    public List<RequestDto> getAllUserItemRequests(Long ownerId) {
        validateIfUserExist(ownerId);
        List<Request> userItemRequests = requestRepository.findAllByOwnerId(ownerId, sortByCreatedDesc);

        return requestMapper.mapStreamToDto(userItemRequests.stream(), itemRepository);
    }

    @Override
    @Transactional
    public List<RequestDto> getAll(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sortByCreatedDesc);

        return requestMapper.mapStreamToDto(
                requestRepository.findAll(pageRequest).stream()
                        .filter(request -> !Objects.equals(userId, request.getRequestId())),
                itemRepository);
    }

    @Override
    public RequestDto getById(Long userId, Long requestId) {
        validateIfUserExist(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotExistsException(
                        "Request",
                        String.format("Request with id $d does not exist", requestId))
        );
        List<ItemDto> itemsDto = itemRepository.findAllByRequest_RequestId(requestId).stream()
                .map(item -> itemMapper.mapToDto(item, requestId))
                .collect(Collectors.toList());

        return requestMapper.mapToRequestDto(request, itemsDto);
    }

    private void validateIfUserExist(Long userId) {
        getUserById(userId);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", userId)
                )
        );
    }
}
