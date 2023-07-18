package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class UserCreateDtoTest {

    @Autowired
    private JacksonTester<UserCreateDto> jacksonTester;

    @SneakyThrows
    @Test
    void testUserCreateDto() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        JsonContent<UserCreateDto> content = jacksonTester.write(userCreateDto);

        Assertions.assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(userCreateDto.getName());
        Assertions.assertThat(content).extractingJsonPathStringValue("$.email")
                .isEqualTo(userCreateDto.getEmail());
    }

}