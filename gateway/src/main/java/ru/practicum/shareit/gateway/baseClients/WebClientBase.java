package ru.practicum.shareit.gateway.baseClients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WebClientBase {
    private final WebClient client;


    @Autowired
    public WebClientBase(@Value("${shareit-server.url}") String serverUrl) {

        this.client = WebClient.builder()
                .baseUrl(serverUrl)
                .filter(logRequest())
                .build();
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public <T> Mono<ResponseEntity<Object>> post(String path, T body) {
        return sendRequest(
                HttpMethod.POST,
                path,
                null,
                null,
                body
        );
    }

    public <T> Mono<ResponseEntity<Object>> post(String path, long userIdHeader, T body) {
        return sendRequest(
                HttpMethod.POST,
                path,
                userIdHeader,
                null,
                body
        );
    }

    public <T> Mono<ResponseEntity<Object>> patch(String path, Long updatedObjectId, T body) {
        return sendRequest(
                HttpMethod.PATCH,
                path + "/" + updatedObjectId,
                null,
                null,
                body
        );
    }

    public <T> Mono<ResponseEntity<Object>> patch(String path, long userIdHeader, Long updatedObjectId, T body) {
        return sendRequest(
                HttpMethod.PATCH,
                path + "/" + updatedObjectId,
                userIdHeader,
                null,
                body
        );
    }

    public <T> Mono<ResponseEntity<Object>> patch(
            String path, long userIdHeader, Long updatedObjectId, Map<String, Object> requestParams) {
        return sendRequest(
                HttpMethod.PATCH,
                path + "/" + updatedObjectId,
                userIdHeader,
                requestParams,
                null
        );
    }

    public Mono<ResponseEntity<Object>> get(String path, Long requestedObjectId) {
        return sendRequest(
                HttpMethod.GET,
                path + "/" + requestedObjectId,
                null,
                null,
                null
        );
    }

    public Mono<ResponseEntity<Object>> get(String path, long userIdHeader, Long requestedObjectId) {
        return sendRequest(
                HttpMethod.GET,
                path + "/" + requestedObjectId,
                userIdHeader,
                null,
                null
        );
    }

    public Mono<ResponseEntity<Object>> get(String path) {
        return sendRequest(
                HttpMethod.GET,
                path,
                null,
                null,
                null
        );
    }

    public Mono<ResponseEntity<Object>> get(String path, long userIdHeader) {
        return sendRequest(
                HttpMethod.GET,
                path,
                userIdHeader,
                null,
                null
        );
    }

    public Mono<ResponseEntity<Object>> get(String path, long userIdHeader, Map<String, Object> requestParams) {
        return sendRequest(
                HttpMethod.GET,
                path,
                userIdHeader,
                requestParams,
                null
        );
    }

    public Mono<ResponseEntity<Object>> delete(String path, Long deletedObjectId) {
        return sendRequest(
                HttpMethod.DELETE,
                path + "/" + deletedObjectId,
                null,
                null,
                null
        );
    }

    public Mono<ResponseEntity<Object>> delete(String path, long userIdHeader, Long deletedObjectId) {
        return sendRequest(
                HttpMethod.DELETE,
                path + "/" + deletedObjectId,
                userIdHeader,
                null,
                null
        );
    }

    private <T> Mono<ResponseEntity<Object>> sendRequest(
            HttpMethod httpMethod, String path, Long userIdHeader, Map<String, Object> requestParams, T body) {

        WebClient.RequestBodySpec requestBodySpec = client
                .method(httpMethod)
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(path);
                    if (requestParams != null) {
                        requestParams.forEach(builder::queryParam);
                    }
                    return builder.build();
                })
                .headers(httpHeaders -> defineHeaders(userIdHeader, httpHeaders));

        if (body != null) {
            requestBodySpec.body(Mono.just(body), Object.class);
        }

        ResponseEntity<Object> response = requestBodySpec
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        resp -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError,
                        resp -> Mono.empty())
                .toEntity(Object.class)
                .block();

        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return Mono.just(ResponseEntity.badRequest().body(response.getBody()));
        }

        return Mono.just(response);
    }

    private void defineHeaders(Long userId, HttpHeaders httpHeaders) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            httpHeaders.set("X-Sharer-User-Id", String.valueOf(userId));
        }
    }
}
