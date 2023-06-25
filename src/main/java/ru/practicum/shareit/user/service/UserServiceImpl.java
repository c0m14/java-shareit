package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEntityException;
import ru.practicum.shareit.exception.NotExistsException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto add(UserCreateDto userCreateDto) {
        User user = mapper.mapToUser(userCreateDto);
        try {
            return mapper.mapToDto(
                    userRepository.save(user)
            );
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntityException(
                    "Email",
                    String.format("User with email %s already exists", user.getEmail())
            );
        }
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User updatedUser = userRepository.findById(userId).orElseThrow(
                () -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", userId)
                )
        );

        updateFields(updatedUser, userDto);

        return mapper.mapToDto(
                userRepository.save(updatedUser)
        );
    }

    @Override
    public UserDto getById(Long id) {
        return mapper.mapToDto(
                userRepository.findById(id).orElseThrow(() -> new NotExistsException(
                        "User",
                        String.format("User with id %d does not exist", id)))
        );
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void checkDuplicateEmail(User user) {
        if (user.getEmail() == null) {
            return;
        }

        Optional<User> userWithSameEmailOptional = userRepository.findByEmail(user.getEmail());
        if (userWithSameEmailOptional.isEmpty()) {
            return;
        }

        if (!userWithSameEmailOptional.get().equals(user)) {
            throw new DuplicateEntityException(
                    "Email",
                    String.format("User with email %s already exists", user.getEmail())
            );
        }
    }

    private void updateFields(User updatedUser, UserDto userDto) {
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
            checkDuplicateEmail(updatedUser);
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
    }

}
