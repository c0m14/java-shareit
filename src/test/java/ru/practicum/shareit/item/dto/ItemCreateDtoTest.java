package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemCreateDtoTest {

    @Autowired
    private JacksonTester<ItemCreateDto> jacksonTester;

    @SneakyThrows
    @Test
    void testItemCreateDto() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();

        JsonContent<ItemCreateDto> content = jacksonTester.write(itemCreateDto);

        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemCreateDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemCreateDto.getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemCreateDto.getAvailable());
        assertThat(content).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemCreateDto.getRequestId().intValue());
    }
}