package ru.practicum.shareit.gateway.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @SneakyThrows
    @Test
    void testCommentDtoIncoming() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .authorName("author")
                .build();

        JsonContent<CommentDto> content = jacksonTester.write(commentDto);

        assertThat(content).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(content).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }

    @SneakyThrows
    @Test
    void testCommentDtoOutComing() {
        CommentDto commentDto = CommentDto.builder()
                .id(0L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> content = jacksonTester.write(commentDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(content).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }

}