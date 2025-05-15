package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoSpecified;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoSpecifiedJsonTest {
    @Autowired
    private JacksonTester<ItemDtoSpecified> jsonTester;

    @Test
    void testSerialize() throws Exception {
        BookingDto nextBooking = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-10-01T10:00:00"))
                .end(LocalDateTime.parse("2023-10-02T10:00:00"))
                .bookerId(10L)
                .status(Status.APPROVED)
                .build();

        BookingDto lastBooking = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.parse("2023-09-01T10:00:00"))
                .end(LocalDateTime.parse("2023-09-02T10:00:00"))
                .bookerId(11L)
                .status(Status.APPROVED)
                .build();

        List<CommentDto> comments = List.of(
                CommentDto.builder()
                        .id(1L)
                        .text("Test comment")
                        .itemId(1L) // Добавляем itemId
                        .authorName("Tester1")
                        .created(LocalDateTime.parse("2023-10-01T10:00:00"))
                        .build()
        );

        ItemDtoSpecified itemDto = ItemDtoSpecified.builder()
                .id(1L)
                .name("Testing test name")
                .description("Test description")
                .available(true)
                .owner(100L)
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .comments(comments)
                .requestId(200L)
                .build();

        JsonContent<ItemDtoSpecified> json = jsonTester.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Testing test name");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(json).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);

        assertThat(json).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(json).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Test comment");
        assertThat(json).extractingJsonPathNumberValue("$.comments[0].itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2023-10-01T10:00:00");

        assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(200);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Test\",\"available\":true," +
                "\"nextBooking\":{\"id\":1,\"start\":\"2023-10-01T10:00:00\",\"status\":\"APPROVED\"}," +
                "\"comments\":[{\"id\":1,\"text\":\"Testing test\",\"itemId\":1," +
                "\"authorName\":\"Tester\",\"created\":\"2023-10-01T10:00:00\"}]}";

        ItemDtoSpecified itemDto = jsonTester.parseObject(jsonContent);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test");
        assertThat(itemDto.getNextBooking().getId()).isEqualTo(1L);
        assertThat(itemDto.getComments().getFirst().getItemId()).isEqualTo(1L);
        assertThat(itemDto.getComments().getFirst().getCreated()).isEqualTo("2023-10-01T10:00:00");
    }
}
