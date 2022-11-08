package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.DuplicateEventException;
import ru.practicum.ewm.exception.EventNotFoundException;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto)
            throws EventNotFoundException {
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable(name = "compId") Long compId) throws CompilationNotFoundException {
        compilationService.delete(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable(name = "compId") Long compId,
                       @PathVariable(name = "eventId") Long eventId) throws CompilationNotFoundException,
            EventNotFoundException {
        compilationService.deleteEvent(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEvent(@PathVariable(name = "compId") Long compId,
                         @PathVariable(name = "eventId") Long eventId) throws DuplicateEventException,
            CompilationNotFoundException, EventNotFoundException {
        compilationService.addEvent(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpin(@PathVariable(name = "compId") Long compId) throws CompilationNotFoundException {
        compilationService.unpin(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pin(@PathVariable(name = "compId") Long compId) throws CompilationNotFoundException {
        compilationService.pin(compId);
    }
}
