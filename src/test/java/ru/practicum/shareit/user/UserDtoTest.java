package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jTester;

    @Test
    void userDtoJsonTest() throws IOException {
        UserDto dto = new UserDto(1, "name", "mail@mail.by");
        JsonContent<UserDto> content = jTester.write(dto);
        Assertions.assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.name").isEqualTo("name");
        Assertions.assertThat(content)
                .extractingJsonPathStringValue("$.email").isEqualTo("mail@mail.by");
    }
}
