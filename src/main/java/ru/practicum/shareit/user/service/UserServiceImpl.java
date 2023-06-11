package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEntityException;
import ru.practicum.shareit.exception.InvalidParamException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        if (userDto.getId() != null) {
            throw new InvalidParamException(
                    "User id",
                    "Id should not been sent in creation request"
            );
        }
        if (userRepository.isEmailExist(userDto.getEmail())) {
            throw new DuplicateEntityException(
                    "Email",
                    String.format("User with email %s already exists", userDto.getEmail())
            );
        }
        User user = userMapper.mapToUser(userDto);
        return userMapper.mapToDto(userRepository.add(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User updatedUser = userRepository.getById(userId).orElseThrow(
                () -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", userId)
                )
        );
        if (!updatedUser.getEmail().equals(userDto.getEmail())) {
            if (userRepository.isEmailExist(userDto.getEmail())) {
                throw new DuplicateEntityException(
                        "Email",
                        String.format("User with email %s already exists", userDto.getEmail())
                );
            }
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }

        return userMapper.mapToDto(userRepository.update(userId, updatedUser));
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> requestedUser = userRepository.getById(id);

        return userMapper.mapToDto(
                requestedUser.orElseThrow(() -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", id)
                )));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

}
