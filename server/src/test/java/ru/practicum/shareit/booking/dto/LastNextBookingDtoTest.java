package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class LastNextBookingDtoTest {

    @Autowired
    private JacksonTester<LastNextBookingDto> jacksonTester;

    @SneakyThrows
    @Test
    void testLastNextBookingDto() {
        LastNextBookingDto lastNextBookingDto = LastNextBookingDto.builder()
                .id(0L)
                .bookerId(1L)
                .build();

        JsonContent<LastNextBookingDto> content = jacksonTester.write(lastNextBookingDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(lastNextBookingDto.getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(lastNextBookingDto.getBookerId().intValue());
    }

}