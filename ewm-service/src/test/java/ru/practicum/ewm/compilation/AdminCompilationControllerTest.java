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
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventMapperImpl;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCompilationController.class)
@AutoConfigureMockMvc
public class AdminCompilationControllerTest {
    @MockBean
    private CompilationServiceImpl compilationService;

    private final EventMapper eventMapper = new EventMapperImpl();

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private CompilationDto compilationDto;

    private NewCompilationDto newCompilationDto;

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

    @BeforeEach
    void setUp() {

        eventShortDtoSet = Set.of(eventMapper.toEventShortDto(getEvent()));
        newCompilationDto = new NewCompilationDto(Set.of(1L), true, "Мюзиклы");
        compilationDto = new CompilationDto(eventShortDtoSet, 1L, true, "Мюзиклы");
    }

    @Test
    void create() throws Exception {
        when(compilationService.create(any(NewCompilationDto.class))).thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.id", is(compilationDto.getId()), Long.class)))
                .andExpect(jsonPath("$.pinned", is(compilationDto.getPinned())))
                .andExpect(jsonPath("$.title", is(compilationDto.getTitle())))
                .andExpect(jsonPath("$.events", hasSize(1)));
    }

    @Test
    void deleteCompilation() throws Exception {
        mvc.perform(delete("/admin/compilations/{compId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEvent() throws Exception {
        mvc.perform(delete("/admin/compilations/{compId}/events/{eventId}", 1L, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addEvent() throws Exception {
        mvc.perform(patch("/admin/compilations/{compId}/events/{eventId}", 1L, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
