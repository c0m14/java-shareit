package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestNoItemsDtoTest {

    @Autowired
    private JacksonTester<RequestNoItemsDto> jacksonTester;

    @SneakyThrows
    @Test
    void testRequestNoItemsDto() {
        RequestNoItemsDto requestNoItemsDto = RequestNoItemsDto.builder()
                .id(0L)
                .created(LocalDateTime.now())
                .description("desc")
                .build();

        JsonContent<RequestNoItemsDto> content = jacksonTester.write(requestNoItemsDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(requestNoItemsDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(requestNoItemsDto.getDescription());
    }

}