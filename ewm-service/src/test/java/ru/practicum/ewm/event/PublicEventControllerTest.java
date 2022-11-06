package ru.practicum.ewm.event;

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
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.stats.StatsClient;
import ru.practicum.ewm.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicEventController.class)
@AutoConfigureMockMvc
class PublicEventControllerTest {

    @MockBean
    private EventServiceImpl eventService;

    @MockBean
    private StatsClient statsClient;

    private final EventMapper eventMapper = new EventMapperImpl();

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private Event event;

    private EventFullDto eventFullDto;

    private EventShortDto eventShortDto;

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

        event.setEventDate(LocalDateTime.now().plusDays(15L).withNano(0));
        event.setInitiator(new User(1L, "user@mail.ru", "Виктор Комаров"));
        event.setLocationLat(55.760016);
        event.setLocationLon(37.615965);
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setPublishedOn(null);
        event.setRequestModeration(true);
        event.setConfirmedRequests(0L);
        event.setTitle("Мюзикл");
        event.setState(EventState.PENDING);
        event.setViews(0L);

        return event;
    }

    @BeforeEach
    void setUp() {
        event = getEvent();
        eventFullDto = eventMapper.toEventFullDto(event);
        eventShortDto = eventMapper.toEventShortDto(event);
    }

    @Test
    void getAll() throws Exception {
        List<EventShortDto> eventShortDtoList = List.of(eventShortDto);

        when(eventService.getAll(any(String.class), any(List.class), any(Boolean.class), any(String.class),
                any(String.class), any(Boolean.class), any(SortType.class),
                any(Integer.class), any(Integer.class)))
                .thenReturn(eventShortDtoList);

        mvc.perform(get("/events")
                        .param("text", "text")
                        .param("categories", "1")
                        .param("paid", "true")
                        .param("rangeStart", "2022-01-06 11:30:00")
                        .param("rangeEnd", "2097-01-06 11:30:00")
                        .param("onlyAvailable", "true")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].annotation", is(eventShortDto.getAnnotation())))
                .andExpect(jsonPath("$[0].category.name", is(eventShortDto.getCategory().getName())))
                .andExpect(jsonPath("$.[0].confirmedRequests", is(eventShortDto.getConfirmedRequests()), Long.class))
                .andExpect(jsonPath("$.[0].eventDate", is(eventShortDto.getEventDate().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.[0].initiator.name", is(eventShortDto.getInitiator().getName())))
                .andExpect(jsonPath("$.[0].paid", is(eventShortDto.getPaid())))
                .andExpect(jsonPath("$.[0].title", is(eventShortDto.getTitle())))
                .andExpect(jsonPath("$.[0].views", is(eventShortDto.getViews()), Long.class));
    }
}