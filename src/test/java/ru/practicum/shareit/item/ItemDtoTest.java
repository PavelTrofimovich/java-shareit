package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jTester;

    @Test
    void itemDtoJsonTest() throws IOException {
        ItemDto dto = new ItemDto(1, "name", "description", true, 1);

        JsonContent<ItemDto> content = jTester.write(dto);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.name").isEqualTo("name");
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.description").isEqualTo("description");
        Assertions.assertThat(content)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}