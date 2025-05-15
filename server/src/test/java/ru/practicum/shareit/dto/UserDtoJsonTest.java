package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> jsonTester;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Tester")
                .email("tester@test.com")
                .build();

        JsonContent<UserDto> json = jsonTester.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Tester");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("tester@test.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Tester\",\"email\":\"tester@test.com\"}";

        UserDto userDto = jsonTester.parseObject(jsonContent);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Tester");
        assertThat(userDto.getEmail()).isEqualTo("tester@test.com");
    }
}
