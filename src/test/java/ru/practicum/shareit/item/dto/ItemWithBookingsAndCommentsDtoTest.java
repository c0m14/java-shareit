package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingsAndCommentsDtoTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    @Autowired
    private JacksonTester<ItemWithBookingsAndCommentsDto> jacksonTester;

    @SneakyThrows
    @Test
    void testItemWithBookingsAndCommentsDto() {
        ItemWithBookingsAndCommentsDto itemWithBookingsAndCommentsDto = ItemWithBookingsAndCommentsDto.builder()
                .id(0L)
                .name("name")
                .description("desc")
                .available(true)
                .comments(List.of(CommentDto.builder()
                        .id(1L)
                        .text("text")
                        .authorName("author")
                        .created(LocalDateTime.now())
                        .build()))
                .lastBooking(LastNextBookingDto.builder()
                        .id(2L)
                        .bookerId(3L)
                        .build())
                .nextBooking(LastNextBookingDto.builder()
                        .id(3L)
                        .bookerId(3L)
                        .build())
                .build();

        JsonContent<ItemWithBookingsAndCommentsDto> content = jacksonTester.write(itemWithBookingsAndCommentsDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemWithBookingsAndCommentsDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemWithBookingsAndCommentsDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemWithBookingsAndCommentsDto.getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemWithBookingsAndCommentsDto.getAvailable());
        assertThat(content).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getText());
        assertThat(content).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getAuthorName());
        assertThat(content).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(itemWithBookingsAndCommentsDto.getComments().get(0).getCreated().format(formatter));
        assertThat(content).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemWithBookingsAndCommentsDto.getLastBooking().getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemWithBookingsAndCommentsDto.getLastBooking().getBookerId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemWithBookingsAndCommentsDto.getNextBooking().getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemWithBookingsAndCommentsDto.getNextBooking().getBookerId().intValue());

    }

}