package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    EventRepository eventRepository;

    @Mock
    RequestRepository requestRepository;

    @Mock
    CategoryRepository categoryRepository;

    private final EventMapper eventMapper = new EventMapperImpl();

    private final RequestMapper requestMapper = new RequestMapperImpl();

    private MockitoSession session;

    private  EventService eventService;

    private static Event event;

    private static UpdateEventRequest updateEventRequest;

    private static Request request;

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
        eventService = new EventServiceImpl(eventRepository, userRepository, categoryRepository, requestRepository,
                eventMapper, requestMapper);
        event = getEvent();
        request = getRequest();
        updateEventRequest = getUpdateEventRequest();
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void getAll() {
        Assertions.assertThrows(DataTimeFormatException.class, () -> eventService.getAll("text", null,
                true, "", null, true, null, 0, 10));
    }

    @Test
    void getByIdEventNotFoundException() {
        Mockito.when(eventRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EventNotFoundException.class, () -> eventService.getById(1L));

        Mockito.verify(eventRepository, Mockito.times(1))
                .findById(Mockito.any(Long.class));
    }

    @Test
    void getByIdEventStatusToViewException() {
        event.setState(EventState.PENDING);
        Mockito.when(eventRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(EventStatusToViewException.class, () -> eventService.getById(1L));

        Mockito.verify(eventRepository, Mockito.times(1))
                .findById(Mockito.any(Long.class));
    }

    @Test
    void updateEventDateNotValidException() {
        updateEventRequest.setEventDate(LocalDateTime.now());
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Mockito.when(categoryRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Assertions.assertThrows(EventDateNotValidException.class, () -> eventService.update(1L,
                updateEventRequest));

        Mockito.verify(eventRepository, Mockito.times(0))
                .save(Mockito.any(Event.class));
    }

    @Test
    void updateUserAccessException() {
        event.setInitiator(new User(2L, "test@mail.ru", "test test"));
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Mockito.when(categoryRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));

        Assertions.assertThrows(UserAccessException.class, () -> eventService.update(1L,
                updateEventRequest));

        Mockito.verify(eventRepository, Mockito.times(0))
                .save(Mockito.any(Event.class));
    }

    @Test
    void updateEventStatusToEditException() {
        event.setState(EventState.PUBLISHED);
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Mockito.when(categoryRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));

        Assertions.assertThrows(EventStatusToEditException.class, () -> eventService.update(1L,
                updateEventRequest));

        Mockito.verify(eventRepository, Mockito.times(0))
                .save(Mockito.any(Event.class));
    }

    @Test
    void confirmRequestRequestConfirmationNotValidException() {
        event.setRequestModeration(false);
        Mockito.when(userRepository.existsById(Mockito.any(Long.class))).thenReturn(true);

        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));

        Mockito.when(requestRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(request));

        Assertions.assertThrows(RequestConfirmationNotValidException.class,
                () -> eventService.confirmRequest(1L, 1L, 1L));
    }

    @Test
    void confirmRequestNotFoundException() {
        event.setRequestModeration(false);
        Mockito.when(userRepository.existsById(Mockito.any(Long.class))).thenReturn(true);

        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));

        Mockito.when(requestRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(RequestNotFoundException.class,
                () -> eventService.confirmRequest(1L, 1L, 1L));
    }

    @Test
    void putEventDataConstraintException() {
        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));

        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenThrow(new DataIntegrityViolationException(""));

        Assertions.assertThrows(EventDataConstraintException.class, () -> eventService.put(1L,
                getAdminUpdateEventRequest()));

        Mockito.verify(eventRepository, Mockito.times(1))
                .save(Mockito.any(Event.class));
    }

    @Test
    void publishEventStatusToPublishException() {
        event.setState(EventState.PUBLISHED);
        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
        Assertions.assertThrows(EventStatusToPublishException.class, () -> eventService.publish(1L));
    }

    @Test
    void publishEventPublishDateNotValidException() {
        event.setState(EventState.PENDING);
        event.setEventDate(LocalDateTime.now());
        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
        Assertions.assertThrows(EventPublishDateNotValidException.class, () -> eventService.publish(1L));
    }

    @Test
    void rejectEventStatusToRejectException() {
        event.setState(EventState.PUBLISHED);
        Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
        Assertions.assertThrows(EventStatusToRejectException.class, () -> eventService.reject(1L));
    }
}