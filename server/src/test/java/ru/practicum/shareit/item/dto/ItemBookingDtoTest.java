package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemBookingDtoTest {

    @Autowired
    private JacksonTester<ItemBookingDto> jacksonTester;

    @SneakyThrows
    @Test
    void testItemBookingDto() {
        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(0L)
                .name("name")
                .build();

        JsonContent<ItemBookingDto> content = jacksonTester.write(itemBookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemBookingDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemBookingDto.getName());
    }

}