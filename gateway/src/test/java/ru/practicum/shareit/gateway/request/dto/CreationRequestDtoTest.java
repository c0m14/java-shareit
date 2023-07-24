package ru.practicum.shareit.gateway.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreationRequestDtoTest {

    @Autowired
    private JacksonTester<CreationRequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testCreationRequestDto() {
        CreationRequestDto creationRequestDto = new CreationRequestDto();
        creationRequestDto.setDescription("desc");

        JsonContent<CreationRequestDto> content = jacksonTester.write(creationRequestDto);

        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(creationRequestDto.getDescription());
    }
}