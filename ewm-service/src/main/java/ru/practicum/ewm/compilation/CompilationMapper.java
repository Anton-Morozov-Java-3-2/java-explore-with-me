package ru.practicum.ewm.compilation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;

import java.util.ArrayList;
import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {EventMapper.class})
public interface CompilationMapper {

    CompilationDto toCompilationDto(Compilation compilation);

    Compilation toCompilation(NewCompilationDto newCompilationDto);

    Set<Event> map(Set<Long> events);

    default Event map(Long value) {
        Event event = new Event();
        event.setId(value);
        event.setReactions(new ArrayList<>());
        return event;
    }
}
