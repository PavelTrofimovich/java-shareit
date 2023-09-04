package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jTester;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

    @Test
    void itemRequestDtoJsonTest() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto(1, "description", created);

        JsonContent<ItemRequestDto> content = jTester.write(dto);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.description").isEqualTo("description");
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.created").isEqualTo(created.format(formatter));
    }
}
