package ru.practicum.ewm.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AdminCategoryController.class)
@AutoConfigureMockMvc
class AdminCategoryControllerTest {

    @MockBean
    private CategoryServiceImpl categoryService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private CategoryDto categoryDto;
    private CategoryDto updateCategoryDto;

    private NewCategoryDto newCategoryDto;


    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto(1L, "test");
        newCategoryDto = new NewCategoryDto("test");
        updateCategoryDto = new CategoryDto(1L, "update");
    }

    @Test
    void create() throws Exception {

        when(categoryService.create(any(NewCategoryDto.class))).thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.id", is(categoryDto.getId()), Long.class)))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void update() throws Exception {
        when(categoryService.update(any(CategoryDto.class))).thenReturn(updateCategoryDto);

        mvc.perform(patch("/admin/categories")
                        .content(mapper.writeValueAsString(updateCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateCategoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateCategoryDto.getName())));
    }
}