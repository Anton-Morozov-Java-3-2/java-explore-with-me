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
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.RequestServiceImpl;
import ru.practicum.ewm.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateEventController.class)
@AutoConfigureMockMvc
class PrivateEventControllerTest {

    @MockBean
    private EventServiceImpl eventService;

    @MockBean
    private RequestServiceImpl requestService;

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private Event event;

    private EventFullDto eventFullDto;

    private EventShortDto eventShortDto;

    private UpdateEventRequest updateEventRequest;

    private NewEventDto newEventDto;

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

    private UpdateEventRequest getUpdateEventRequest() {

        UpdateEventRequest event = new UpdateEventRequest();
        event.setEventId(1L);
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
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setTitle("Мюзикл");

        return event;
    }

    private NewEventDto getNewEventDto() {

        NewEventDto event = new NewEventDto();

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
        event.setLocation(new Location(55.760016, 37.615965));
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setRequestModeration(true);
        event.setTitle("Мюзикл");

        return event;
    }

    @BeforeEach
    void setUp() {
        event = getEvent();
        eventFullDto = EventMapper.INSTANCE.toEventFullDto(event);
        eventShortDto = EventMapper.INSTANCE.toEventShortDto(event);
        updateEventRequest = getUpdateEventRequest();
        newEventDto = getNewEventDto();
    }

    @Test
    void getAllByUserId() throws Exception {
        List<EventShortDto> eventShortDtoList = List.of(eventShortDto);

        when(eventService.getAllByUserId(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(eventShortDtoList);

        mvc.perform(get("/users/{userId}/events",1L)
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].annotation", is(eventShortDto.getAnnotation())))
                .andExpect(jsonPath("$[0].category.id", is(eventShortDto.getCategory().getId()), Long.class))
                .andExpect(jsonPath("$[0].category.name", is(eventShortDto.getCategory().getName())))
                .andExpect(jsonPath("$.[0].eventDate", is(eventShortDto.getEventDate().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.[0].initiator.name", is(eventShortDto.getInitiator().getName())))
                .andExpect(jsonPath("$.[0].paid", is(eventShortDto.getPaid())))
                .andExpect(jsonPath("$.[0].title", is(eventShortDto.getTitle())))
                .andExpect(jsonPath("$.[0].views", is(eventShortDto.getViews()), Long.class));
    }

    @Test
    void update() throws Exception {
        when(eventService.update(any(Long.class), any(UpdateEventRequest.class)))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/{userId}/events", 1L)
                        .content(mapper.writeValueAsString(updateEventRequest))
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

    @Test
    void create() throws Exception {
        when(eventService.create(any(Long.class), any(NewEventDto.class)))
                .thenReturn(eventFullDto);

        mvc.perform(post("/users/{userId}/events", 1L)
                        .content(mapper.writeValueAsString(newEventDto))
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

    @Test
    void getById() throws Exception {
        when(eventService.getByEventId(any(Long.class), any(Long.class)))
                .thenReturn(eventFullDto);

        mvc.perform(get("/users/{userId}/events/{eventId}", 1L, 1L)
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