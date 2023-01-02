package ru.practicum.ewm.reaction;

import org.mapstruct.Mapper;
import ru.practicum.ewm.reaction.dto.ReactionDto;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    ReactionDto toDto(Reaction reaction);
}
