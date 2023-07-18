package ru.practicum.shareit.gateway.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.BaseClient;
import ru.practicum.shareit.gateway.request.dto.CreationRequestDto;
import ru.practicum.shareit.gateway.request.dto.requestParams.GetAllRequestParams;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String REQUESTS_API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + REQUESTS_API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(long userId, CreationRequestDto creationRequestDto) {
        return post("", userId, creationRequestDto);
    }

    public ResponseEntity<Object> getAllRequestsByOwnerId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllOtherUsersRequests(long userId, GetAllRequestParams requestParams) {
        Map<String, Object> params = Map.of(
                "from", requestParams.getFrom(),
                "size", requestParams.getSize()
        );
        return get("/all?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getRequestById(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
