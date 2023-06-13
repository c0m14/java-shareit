package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User add(User user);

    User update(Long userId, User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    void delete(Long id);

    boolean isEmailExist(String email);
}
