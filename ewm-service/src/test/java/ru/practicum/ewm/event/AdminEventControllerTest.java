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
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.Location;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
@AutoConfigureMockMvc
class AdminEventControllerTest {

    @MockBean
    private EventServiceImpl eventService;

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private EventFullDto eventFullDto;

    private AdminUpdateEventRequest adminUpdateEventRequest;

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
        event.setTitle("Мюзикл");
        event.setState(EventState.PENDING);
        event.setViews(0L);

        return event;
    }

    private AdminUpdateEventRequest getAdminUpdateEventRequest() {

        AdminUpdateEventRequest event = new AdminUpdateEventRequest();

        event.setAnnotation("Эстрадный мюзикл по роману Льва Толстого");
        event.setCategory(1L);
        event.setDescription("Мюзикл «Московской оперетты» поставлен с эстрадным размахом. " +
                "В оркестровой яме в основном духовые, а из динамиков грохочут барабаны с " +
                "электрогитарой в духе рок-опер восьмедисятых. Вместо декораций — огромный " +
                "экран, превращающий место действия то в вокзал, то в бальный зал, то в " +
                "зимнюю площадь с золотыми куполами и праздничным катком. Либретто написал " +
                "Юлий Ким, поставила спектакль Алина Чевик, музыку сочинил Роман Игнатьев. " +
                "Главный хит: «Не ходите по пу, по путям». Заглавную роль в новом шоу исполняют, " +
                "сменяя друг друга, Екатерина Гусева и Валерия Ланская.");

        event.setEventDate(LocalDateTime.now().plusDays(15L).withNano(0));
        event.setLocation(new Location(55.760016,37.615965));
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setRequestModeration(true);
        event.setTitle("Мюзикл");
        return event;
    }

    @BeforeEach
    void setUp() {
        eventFullDto = EventMapper.INSTANCE.toEventFullDto(getEvent());
        adminUpdateEventRequest = getAdminUpdateEventRequest();
    }

    @Test
    void getAll() throws Exception {
        List<EventFullDto> eventFullDtoList = List.of(eventFullDto);

        when(eventService.getAll(any(), any(), any(), any(String.class), any(String.class),
                any(Integer.class), any(Integer.class)))
                .thenReturn(eventFullDtoList);

        mvc.perform(get("/admin/events")
                        .param("users", "1")
                        .param("states", "PENDING")
                        .param("categories", "1")
                        .param("rangeStart", "2022-01-06 11:30:00")
                        .param("rangeEnd", "2097-01-06 11:30:00")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventFullDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].annotation", is(eventFullDto.getAnnotation())))
                .andExpect(jsonPath("$[0].category.id", is(eventFullDto.getCategory().getId()), Long.class))
                .andExpect(jsonPath("$[0].category.name", is(eventFullDto.getCategory().getName())))
                .andExpect(jsonPath("$.[0].createdOn", is(eventFullDto.getCreatedOn().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.[0].description", is(eventFullDto.getDescription())))
                .andExpect(jsonPath("$.[0].eventDate", is(eventFullDto.getEventDate().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.[0].initiator.name", is(eventFullDto.getInitiator().getName())))
                .andExpect(jsonPath("$.[0].location.lat", is(eventFullDto.getLocation().getLat()),Double.class))
                .andExpect(jsonPath("$.[0].location.lon", is(eventFullDto.getLocation().getLon()),Double.class))
                .andExpect(jsonPath("$.[0].paid", is(eventFullDto.getPaid())))
                .andExpect(jsonPath("$.[0].participantLimit", is(eventFullDto.getParticipantLimit())))
                .andExpect(jsonPath("$.[0].publishedOn", is(eventFullDto.getPublishedOn())))
                .andExpect(jsonPath("$.[0].requestModeration", is(eventFullDto.getRequestModeration())))
                .andExpect(jsonPath("$.[0].state", is(eventFullDto.getState().toString())))
                .andExpect(jsonPath("$.[0].title", is(eventFullDto.getTitle())))
                .andExpect(jsonPath("$.[0].views", is(eventFullDto.getViews()), Long.class));
    }

    @Test
    void putEvent() throws Exception {
        when(eventService.put(any(Long.class), any(AdminUpdateEventRequest.class)))
                .thenReturn(eventFullDto);

        mvc.perform(put("/admin/events/{eventId}", 1L)
                        .content(mapper.writeValueAsString(adminUpdateEventRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation())))
                .andExpect(jsonPath("$.category.id", is(eventFullDto.getCategory().getId()), Long.class))
                .andExpect(jsonPath("$.category.name", is(eventFullDto.getCategory().getName())))
                .andExpect(jsonPath("$.createdOn", is(eventFullDto.getCreatedOn().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())))
                .andExpect(jsonPath("$.eventDate", is(eventFullDto.getEventDate().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.initiator.name", is(eventFullDto.getInitiator().getName())))
                .andExpect(jsonPath("$.location.lat", is(eventFullDto.getLocation().getLat()),Double.class))
                .andExpect(jsonPath("$.location.lon", is(eventFullDto.getLocation().getLon()),Double.class))
                .andExpect(jsonPath("$.paid", is(eventFullDto.getPaid())))
                .andExpect(jsonPath("$.participantLimit", is(eventFullDto.getParticipantLimit())))
                .andExpect(jsonPath("$.publishedOn", is(eventFullDto.getPublishedOn())))
                .andExpect(jsonPath("$.requestModeration", is(eventFullDto.getRequestModeration())))
                .andExpect(jsonPath("$.state", is(eventFullDto.getState().toString())))
                .andExpect(jsonPath("$.title", is(eventFullDto.getTitle())))
                .andExpect(jsonPath("$.views", is(eventFullDto.getViews()), Long.class));
    }
}