package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceTest {

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    EventRepository eventRepository;

    private RequestService requestService;

    private final RequestMapper requestMapper = new RequestMapperImpl();

    private MockitoSession session;

    private static ParticipationRequestDto requestDto;

    private static Request request;

    private static Event event;

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

        event.setEventDate(LocalDateTime.now().plusDays(15L));
        event.setInitiator(new User(1L, "user@mail.ru", "Виктор Комаров"));
        event.setLocationLat(55.760016);
        event.setLocationLon(37.615965);
        event.setPaid(true);
        event.setParticipantLimit(1000);
        event.setConfirmedRequests(0L);
        event.setPublishedOn(null);
        event.setRequestModeration(true);
        event.setTitle("Мюзикл");
        event.setState(EventState.PENDING);
        event.setViews(0L);

        return event;
    }

    private Request getRequest() {
        Request request = new Request();
        request.setRequester(new User(2L, "user@email.ru", "Виктор Комаров"));
        request.setEvent(event);
        request.setCreated(LocalDateTime.now().withNano(0));
        request.setStatus(RequestState.PENDING);
        return request;
    }

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        event = getEvent();
        requestService = new RequestServiceImpl(requestRepository, userRepository, eventRepository, requestMapper);
        request = getRequest();
        requestDto = requestMapper.toParticipationRequestDto(request);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void getAllByUserId() throws UserNotFoundException {
        Mockito.when(requestRepository.findAllByRequesterId(Mockito.any(Long.class)))
                .thenReturn(List.of(request));

        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Assertions.assertArrayEquals(List.of(requestDto).toArray(),
                requestService.getAllByUserId(1L).toArray());

        Mockito.verify(requestRepository, Mockito.times(1))
                .findAllByRequesterId(Mockito.any(Long.class));
    }

    @Test
    void create() throws UserNotFoundException, EventNotFoundException, ParticipantLimitExceedException, UserAccessException, DuplicateRequestException {
        event.setInitiator(new User(3L, "test@mail.ru", "Test Test"));
        event.setState(EventState.PUBLISHED);
        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(new User(1L, "user@email.ru", "Виктор Комаров")));

        Mockito.when(eventRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(event));

        Mockito.when(requestRepository.findByEventIdAndRequesterId(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Mockito.when(requestRepository.save(Mockito.any(Request.class)))
                .thenReturn(request);

        Assertions.assertEquals(requestDto,
                requestService.create(1L, 1L));

        Mockito.verify(requestRepository, Mockito.times(1))
                .save(Mockito.any(Request.class));
    }

    @Test
    void createDuplicateRequestException() {
        event.setInitiator(new User(3L, "test@mail.ru", "Test Test"));
        event.setState(EventState.PUBLISHED);
        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(new User(1L, "user@email.ru", "Виктор Комаров")));

        Mockito.when(eventRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(event));

        Mockito.when(requestRepository.findByEventIdAndRequesterId(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(Optional.of(request));

        Assertions.assertThrows(DuplicateRequestException.class, () -> requestService.create(1L, 1L));

        Mockito.verify(requestRepository, Mockito.times(0))
                .save(Mockito.any(Request.class));
    }

    @Test
    void createParticipantLimitExceedException() {
        event.setInitiator(new User(3L, "test@mail.ru", "Test Test"));
        event.setState(EventState.PUBLISHED);
        event.setParticipantLimit(3);
        event.setConfirmedRequests(3L);
        Mockito.when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(new User(1L, "user@email.ru", "Виктор Комаров")));

        Mockito.when(eventRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(event));

        Mockito.when(requestRepository.findByEventIdAndRequesterId(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ParticipantLimitExceedException.class,
                () -> requestService.create(1L, 1L));

        Mockito.verify(requestRepository, Mockito.times(0))
                .save(Mockito.any(Request.class));
    }
}