package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testRequestDto() {
        RequestDto requestDto = RequestDto.builder()
                .created(LocalDateTime.now().minusHours(1))
                .description("desc")
                .id(0L)
                .items(List.of(ItemDto.builder()
                        .id(1L)
                        .requestId(0L)
                        .name("item")
                        .description("description")
                        .available(true)
                        .build()))
                .build();

        JsonContent<RequestDto> content = jacksonTester.write(requestDto);

        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(requestDto.getDescription());
        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(requestDto.getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(requestDto.getItems().get(0).getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(requestDto.getItems().get(0).getRequestId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(requestDto.getItems().get(0).getName());
        assertThat(content).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(requestDto.getItems().get(0).getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(requestDto.getItems().get(0).getAvailable());
    }

}