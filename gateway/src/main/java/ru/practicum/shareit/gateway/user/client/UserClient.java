package ru.practicum.shareit.gateway.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.BaseClient;
import ru.practicum.shareit.gateway.user.dto.UserCreateDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String USERS_API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + USERS_API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addUser(UserCreateDto userCreateDto) {
        return post("", userCreateDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> deleteUserById(Long userId) {
        return delete("/" + userId);
    }
}
