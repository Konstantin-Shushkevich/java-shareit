package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> jsonTester;

    @Test
    void testSerialize() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Testing text")
                .itemId(1L)
                .authorName("Tester")
                .created(LocalDateTime.parse("2023-10-01T10:00:00"))
                .build();

        JsonContent<CommentDto> json = jsonTester.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Testing text");
        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("Tester");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T10:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent =
                "{\"id\":1,\"text\":\"Testing text\",\"itemId\":1,\"authorName\":\"Tester\",\"created\":\"2023-10-01T10:00:00\"}";

        CommentDto commentDto = jsonTester.parseObject(jsonContent);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Testing text");
        assertThat(commentDto.getItemId()).isEqualTo(1L);
        assertThat(commentDto.getAuthorName()).isEqualTo("Tester");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.parse("2023-10-01T10:00:00"));
    }

    @Test
    void testHandleNullFields() throws Exception {
        String jsonContent = "{\"id\":1,\"text\":\"Тест\"}";

        CommentDto commentDto = jsonTester.parseObject(jsonContent);

        assertThat(commentDto.getItemId()).isNull();
        assertThat(commentDto.getAuthorName()).isNull();
        assertThat(commentDto.getCreated()).isNull();
    }
}
