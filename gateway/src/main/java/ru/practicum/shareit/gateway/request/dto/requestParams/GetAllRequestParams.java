package ru.practicum.shareit.gateway.request.dto.requestParams;

import lombok.Data;

@Data
public class GetAllRequestParams {
    private final Long ownerId;
    private final int from;
    private final int size;
}
