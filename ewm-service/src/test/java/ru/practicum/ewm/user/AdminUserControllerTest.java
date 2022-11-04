package ru.practicum.ewm.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.exception.UserEmailNotUniqueException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc
public class AdminUserControllerTest {
    @MockBean
    private UserServiceImpl userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    private NewUserRequest userRequest;

    @Test
    void testCreate() throws Exception {
        userRequest = new NewUserRequest("email@email.ru", "Test");
        userDto = new UserDto(1L, "email@email.ru", "Test");

        when(userService.create(any(NewUserRequest.class))).thenReturn(userDto);

        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(userRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.id", is(userDto.getId()), Long.class)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect((jsonPath("$.email", is(userDto.getEmail()))));
    }

    @Test
    void testCreateDuplicateEmail() throws Exception {
        userRequest = new NewUserRequest("email@email.ru", "Test");
        userDto = new UserDto(1L, "email@email.ru", "Test");

        when(userService.create(any(NewUserRequest.class))).thenThrow(UserEmailNotUniqueException.class);

        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(userRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.reason", is("Integrity constraint has been violated")));
    }
}
