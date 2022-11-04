package ru.practicum.ewm.compilation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PublicCompilationController.class)
@AutoConfigureMockMvc
class PublicCompilationControllerTest {

    @MockBean
    private CompilationServiceImpl compilationService;

    private final ObjectMapper mapper = new ObjectMapper();

    private CompilationDto compilationDto;

    private Set<EventShortDto> eventShortDtoSet;

    private Event getEvent() {

        Event event = new Event();

        event.setAnnotation("Эстрадный мюзикл по роману Льва Толстого");
        event.setCategory(new Category(1L, "category"));
        event.setCreatedOn(LocalDateTime.now().withNano(0));
        event.setDescription("Мюзикл «Московской оперетты» поставлен с эстрадным размахом. " +
                "В оркестровой яме в основном духовые, а из динамиков грохочут барабаны с " +
                "электрогитарой в духе рок-опер восьмедисятых. Вместо декораций — огромный " +
                "экран, превращающий место действия то в вокзал, то в бальный зал, то в " +
                "зимнюю площадь с золотыми куполами и праздничным катком. Либретто написал " +
                "Юлий Ким, поставила спектакль Алина Чевик, музыку сочинил Роман Игнатьев. " +
                "Главный хит: «Не ходите по пу, по путям». Заглавную роль в новом шоу исполняют, " +
                "сменяя друг друга, Екатерина Гусева и Валерия Ланская.");

        event.setEventDate(LocalDateTime.now().plusDays(15L));
        event.setInitiator(new User(1L, "user@mail.ru", "Виктор Комаров"));
        event.setLocationLat(55.760016);
        event.setLocationLon(37.615965);
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setPublishedOn(null);
        event.setRequestModeration(true);
        event.setTitle("Мюзикл");
        event.setState(EventState.PENDING);
        event.setViews(0L);

        return event;
    }

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        eventShortDtoSet = Set.of(EventMapper.INSTANCE.toEventShortDto(getEvent()));
        compilationDto = new CompilationDto(eventShortDtoSet, 1L, true, "Мюзиклы");
    }

    @Test
    void getAll() throws Exception {
        List<CompilationDto> compilationDtoList = List.of(compilationDto);

        when(compilationService.getAll(any(Boolean.class), any(Integer.class), any(Integer.class)))
                .thenReturn(compilationDtoList);

        mvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(compilationDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(compilationDtoList.get(0).getTitle())))
                .andExpect(jsonPath("$[0].pinned", is(compilationDtoList.get(0).getPinned())))
                .andExpect(jsonPath("$[0].events", hasSize(1)));
    }

    @Test
    void getById() throws Exception {
        List<CompilationDto> compilationDtoList = List.of(compilationDto);

        when(compilationService.getById(any(Long.class)))
                .thenReturn(compilationDto);

        mvc.perform(get("/compilations/{compId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(compilationDto.getId()), Long.class))
                .andExpect(jsonPath("$.title", is(compilationDto.getTitle())))
                .andExpect(jsonPath("$.pinned", is(compilationDto.getPinned())))
                .andExpect(jsonPath("$.events", hasSize(1)));
    }
}