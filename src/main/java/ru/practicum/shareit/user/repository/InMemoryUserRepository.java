package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> repository = new HashMap<>();
    private long idCounter = 1;

    @Override
    public User add(User user) {
        user.setId(idCounter);
        idCounter++;
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        repository.put(userId, user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        User requestedUser = repository.get(id);
        if (requestedUser == null) {
            return Optional.empty();
        }
        return Optional.of(requestedUser);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public void delete(Long id) {
        repository.remove(id);
    }

    @Override
    public boolean isEmailExist(String email) {
        Optional<String> duplicateEmail = repository.values().stream()
                .map(User::getEmail)
                .filter(savedEmail -> savedEmail.equals(email))
                .findFirst();

        return duplicateEmail.isPresent();
    }
}