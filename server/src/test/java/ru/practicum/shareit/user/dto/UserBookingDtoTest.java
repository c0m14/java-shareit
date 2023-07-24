package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class UserBookingDtoTest {

    @Autowired
    private JacksonTester<UserBookingDto> jacksonTester;

    @SneakyThrows
    @Test
    void testUserBookingDto() {
        UserBookingDto userBookingDto = new UserBookingDto(0L);

        JsonContent<UserBookingDto> content = jacksonTester.write(userBookingDto);

        Assertions.assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(userBookingDto.getId().intValue());
    }

}