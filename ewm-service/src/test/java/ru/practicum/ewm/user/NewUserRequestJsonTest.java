package ru.practicum.ewm.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewUserRequestJsonTest {
    @Autowired
    private JacksonTester<NewUserRequest> jsonNewUserRequest;

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Test
    void testSerialize() throws Exception {
        var dtoRequest = new NewUserRequest("test@mail.ru", "test");
        var dtoUser = new UserDto(1L, "test@mail.ru", "test");
        var resultRequest = jsonNewUserRequest.write(dtoRequest);
        var resultDto = jsonUserDto.write(dtoUser);


        assertThat(resultRequest).hasJsonPath("$.name");
        assertThat(resultRequest).hasJsonPath("$.email");

        assertThat(resultDto).hasJsonPath("$.id");
        assertThat(resultDto).hasJsonPath("$.name");
        assertThat(resultDto).hasJsonPath("$.email");

        assertThat(resultRequest).extractingJsonPathStringValue("$.name").isEqualTo(dtoRequest.getName());
        assertThat(resultRequest).extractingJsonPathStringValue("$.email").isEqualTo(dtoRequest.getEmail());

        assertThat(resultDto).extractingJsonPathNumberValue("$.id").isEqualTo(dtoUser.getId().intValue());
        assertThat(resultDto).extractingJsonPathStringValue("$.name").isEqualTo(dtoUser.getName());
        assertThat(resultDto).extractingJsonPathStringValue("$.email").isEqualTo(dtoUser.getEmail());
    }
}
