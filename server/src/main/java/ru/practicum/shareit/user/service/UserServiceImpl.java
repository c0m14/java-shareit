package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotExistsException(
                    "User",
                    String.format("User with id %d does not exist", id)
            );
        }
    }

    private void checkDuplicateEmail(String email, Long userId) {

        Optional<User> userWithSameEmailOptional = userRepository.findByEmail(email);
        if (userWithSameEmailOptional.isEmpty()) {
            return;
        }

        if (!userWithSameEmailOptional.get().getId().equals(userId)) {
            throw new DuplicateEntityException(
                    "Email",
                    String.format("User with email %s already exists", email)
            );
        }
    }

    private void updateFields(User updatedUser, UserDto userDto) {
        if (userDto.getEmail() != null) {
            checkDuplicateEmail(userDto.getEmail(), updatedUser.getId());
            updatedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
    }

}
