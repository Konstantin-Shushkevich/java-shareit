package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> jsonTester;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item")
                .description("Testing tester's item for test")
                .available(true)
                .owner(10L)
                .start(LocalDateTime.parse("2023-10-01T10:00:00"))
                .end(LocalDateTime.parse("2023-10-02T10:00:00"))
                .requestId(100L)
                .build();

        JsonContent<ItemDto> json = jsonTester.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Test item");
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo("Testing tester's item for test");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(json).extractingJsonPathNumberValue("$.owner").isEqualTo(10);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-02T10:00:00");
        assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(100);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Item test\",\"description\":\"Testing tester's item for test\"," +
                "\"available\":true,\"owner\":10,\"start\":\"2023-10-01T10:00:00\"," +
                "\"end\":\"2023-10-02T10:00:00\",\"requestId\":100}";

        ItemDto itemDto = jsonTester.parseObject(jsonContent);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item test");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getStart()).isEqualTo("2023-10-01T10:00:00");
        assertThat(itemDto.getRequestId()).isEqualTo(100L);
    }
}
