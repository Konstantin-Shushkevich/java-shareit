package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonTester;

    @Test
    void testSerialize() throws Exception {
        List<ItemDto> items = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Test item")
                        .available(true)
                        .build()
        );

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Test description")
                .requester(10L)
                .created(LocalDateTime.parse("2023-10-01T10:00:00"))
                .items(items)
                .build();

        JsonContent<ItemRequestDto> json = jsonTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Test description");
        assertThat(json).extractingJsonPathNumberValue("$.requester").isEqualTo(10);
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T10:00:00");
        assertThat(json).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(json).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Test item");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"description\":\"Test description\",\"requester\":10,\"created\":\"2023-10-01T10:00:00\",\"items\":[{\"id\":1,\"name\":\"Test item\",\"available\":true}]}";

        ItemRequestDto dto = jsonTester.parseObject(jsonContent);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Test description");
        assertThat(dto.getRequester()).isEqualTo(10L);
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.parse("2023-10-01T10:00:00"));
        assertThat(dto.getItems().getFirst().getName()).isEqualTo("Test item");
    }
}
