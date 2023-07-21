package ru.practicum.shareit.gateway.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.baseClients.BaseClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.requestParam.GetByUserRequestParams;
import ru.practicum.shareit.gateway.item.dto.requestParam.SearchRequestParams;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String ITEMS_API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ITEMS_API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> addComment(long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateItem(long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUsersItems(long userId, GetByUserRequestParams requestParams) {
        Map<String, Object> params = Map.of(
                "from", requestParams.getFrom(),
                "size", requestParams.getSize()
        );
        return get("?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> searchItems(long userId, SearchRequestParams requestParams) {
        Map<String, Object> params = Map.of(
                "from", requestParams.getFrom(),
                "size", requestParams.getSize(),
                "text", requestParams.getText()
        );
        return get("/search?text={text}&from={from}&size={size}", userId, params);
    }
}
