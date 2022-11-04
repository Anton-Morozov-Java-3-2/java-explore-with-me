package ru.practicum.ewm.request;

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
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateRequestController.class)
@AutoConfigureMockMvc
class PrivateRequestControllerTest {

    @MockBean
    private RequestServiceImpl requestService;

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private Request request;

    private ParticipationRequestDto requestDto;

    private Event getEvent() {

        Event event = new Event();
        event.setId(1L);
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

    private Request getRequest() {
        Request request = new Request();
        request.setRequester(new User(1L, "user@email.ru", "Виктор Комаров"));
        request.setEvent(getEvent());
        request.setCreated(LocalDateTime.now().withNano(0));
        request.setStatus(RequestState.PENDING);
        return request;
    }

    @BeforeEach
    void setUp() {
        request = getRequest();
        requestDto = RequestMapper.INSTANCE.toParticipationRequestDto(request);
    }

    @Test
    void getAllByUserId() throws Exception {
        List<ParticipationRequestDto> list = List.of(requestDto);

        when(requestService.getAllByUserId(any(Long.class)))
                .thenReturn(list);

        mvc.perform(get("/users/{userId}/requests", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].created", is(requestDto.getCreated().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.[0].requester", is(requestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.[0].event", is(requestDto.getEvent()),Long.class))
                .andExpect(jsonPath("$.[0].status", is(requestDto.getStatus().name())));
    }

    @Test
    void create() throws Exception {
        when(requestService.create(any(Long.class), any(Long.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/users/{userId}/requests", 1L)
                        .param("eventId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.requester", is(requestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.event", is(requestDto.getEvent()), Long.class))
                .andExpect(jsonPath("$.status", is(requestDto.getStatus().name())));
    }
}