package ru.practicum.ewm.reaction.dto;

import lombok.Value;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.reaction.TypeReaction;
import ru.practicum.ewm.user.dto.UserShortDto;

@Value
public class ReactionDto {
    Long id;
    UserShortDto user;
    EventShortDto event;
    TypeReaction reaction;
}
