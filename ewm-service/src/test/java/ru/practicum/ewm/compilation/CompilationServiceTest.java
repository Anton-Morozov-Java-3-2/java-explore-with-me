package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.DuplicateEventException;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.user.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CompilationServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    CompilationRepository compilationRepository;

    CompilationService compilationService;

    EventMapper eventMapper = new EventMapperImpl();

    CompilationMapper compilationMapper = new CompilationMapperImpl(eventMapper);


    private MockitoSession session;

    public static Event event;

    public static Compilation compilation;

    public static CompilationDto compilationDto;

    public static NewCompilationDto newCompilationDto;

    public static Set<EventShortDto> eventShortDtoSet;

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
        event.setReactions(new ArrayList<>());
        return event;
    }


    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        event = getEvent();
        compilationService = new CompilationServiceImpl(compilationRepository, eventRepository, compilationMapper);
        compilation = new Compilation(1L, Set.of(event), true, "Мюзиклы");
        eventShortDtoSet = Set.of(eventMapper.toEventShortDto(event));
        compilationDto = new CompilationDto(eventShortDtoSet, 1L, true, "Мюзиклы");
        newCompilationDto = new NewCompilationDto(Set.of(1L), true, "Мюзиклы");
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void getAll() {
        Mockito.when(compilationRepository.findAllByPinned(Mockito.any(Boolean.class), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(compilation)));

        Assertions.assertArrayEquals(List.of(compilationDto).toArray(),
                compilationService.getAll(true, 0, 10).toArray());

        Mockito.verify(compilationRepository, Mockito.times(1))
                .findAllByPinned(Mockito.any(Boolean.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getById() throws CompilationNotFoundException {
        Mockito.when(compilationRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(compilation));

        Assertions.assertEquals(compilationDto,
                compilationService.getById(1L));

        Mockito.verify(compilationRepository, Mockito.times(1))
                .findById(Mockito.any(Long.class));
    }

    @Test
    void create() throws EventNotFoundException {
        Mockito.when(compilationRepository.save(Mockito.any(Compilation.class)))
                .thenReturn(compilation);

        Mockito.when(eventRepository.adminFindByIds(Mockito.any(Set.class)))
                .thenReturn(Set.of(event));

        Assertions.assertEquals(compilationDto,
                compilationService.create(newCompilationDto));

        Mockito.verify(compilationRepository, Mockito.times(1))
                .save(Mockito.any(Compilation.class));
    }

    @Test
    void delete() {
        Mockito.when(compilationRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Assertions.assertDoesNotThrow(() -> compilationService.delete(1L));


        Mockito.verify(compilationRepository, Mockito.times(1))
                .deleteById(Mockito.any(Long.class));
    }

    @Test
    void deleteCompilationNotFoundException() {

        Mockito.when(compilationRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(false);

        Assertions.assertThrows(CompilationNotFoundException.class, () -> compilationService.delete(1L));


        Mockito.verify(compilationRepository, Mockito.times(0))
                .deleteById(Mockito.any(Long.class));
    }

    @Test
    void addEventDuplicateEventException() {
        Mockito.when(compilationRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(compilation));

        Mockito.when(eventRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(event));

        Assertions.assertThrows(DuplicateEventException.class, () -> compilationService.addEvent(1L, 2L));

        Mockito.verify(compilationRepository, Mockito.times(0))
                .save(Mockito.any(Compilation.class));
    }
}