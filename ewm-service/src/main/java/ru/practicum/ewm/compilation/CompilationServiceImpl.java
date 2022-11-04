package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.DuplicateEventException;
import ru.practicum.ewm.exception.EventNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        Page<Compilation> compilations = (pinned == null ? compilationRepository.findAll(pageRequest) :
                compilationRepository.findAllByPinned(pinned, pageRequest));
            log.info("Get all compilations");
            return compilations.stream().map(CompilationMapper.INSTANCE::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(CompilationNotFoundException.createMessage(compId)));
        log.info("Get {}", compilation);
        return CompilationMapper.INSTANCE.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) throws EventNotFoundException {
        Compilation compilation = CompilationMapper.INSTANCE.toCompilation(newCompilationDto);
        if (compilation.getPinned() == null)
            compilation.setPinned(false);
        Set<Long> eventIds = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toSet());
        Set<Event> events = new HashSet<>();

        for (Long e : eventIds) {
            events.add(eventRepository.findById(e).orElseThrow(() -> new EventNotFoundException(EventNotFoundException
                    .createMessage(e))));
        }

        compilation.setEvents(events);
        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Create {}", newCompilation);
        return CompilationMapper.INSTANCE.toCompilationDto(newCompilation);
    }

    @Override
    public void delete(Long compId) throws CompilationNotFoundException {
        checkExistCompilation(compId);
        compilationRepository.deleteById(compId);
        log.info("Delete compilation id={}", compId);
    }

    @Override
    public void deleteEvent(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(CompilationNotFoundException.createMessage(compId)));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));
        Set<Event> events = compilation.getEvents();
        if (events.contains(event)) {
            compilation.getEvents().remove(event);
        } else {
            throw new EventNotFoundException(String.format("Event id=%d not found in compilation id=%d", eventId, compId));
        }

        Compilation updateCompilation = compilationRepository.save(compilation);
        log.info("Event id={} remove from compilation {}", eventId, updateCompilation);
    }

    @Override
    public void addEvent(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException, DuplicateEventException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(CompilationNotFoundException.createMessage(compId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        Set<Event> events = compilation.getEvents();

        if (events.contains(event)) {
            throw new DuplicateEventException("Event id=%d already in compilation id=%d");
        }

        compilation.getEvents().add(event);
        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Event id={} add to compilation {}", eventId, newCompilation);
    }

    @Override
    public void unpin(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(CompilationNotFoundException.createMessage(compId)));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Compilation id={} unpin", compId);
    }

    @Override
    public void pin(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(CompilationNotFoundException.createMessage(compId)));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.info("Compilation id={} unpin", compId);
    }

    private void checkExistCompilation(Long compId) throws CompilationNotFoundException {
        if (!compilationRepository.existsById(compId))
            throw new CompilationNotFoundException(CompilationNotFoundException.createMessage(compId));
    }
}
