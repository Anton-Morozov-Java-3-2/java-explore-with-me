package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.DuplicateEventException;
import ru.practicum.ewm.exception.EventNotFoundException;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId) throws CompilationNotFoundException;

    CompilationDto create(NewCompilationDto newCompilationDto) throws EventNotFoundException;

    void delete(Long compId) throws CompilationNotFoundException;

    void deleteEvent(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException;

    void addEvent(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException, DuplicateEventException;

    void unpin(Long compId) throws CompilationNotFoundException;

    void pin(Long compId) throws CompilationNotFoundException;
}
