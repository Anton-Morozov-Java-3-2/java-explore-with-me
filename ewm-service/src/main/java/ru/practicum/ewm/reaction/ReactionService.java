package ru.practicum.ewm.reaction;

import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.reaction.dto.ReactionDto;
import ru.practicum.ewm.reaction.dto.UserRatingDto;

import java.util.List;

public interface ReactionService {

    ReactionDto create(Long userId, Long eventId, TypeReaction reaction) throws UserNotFoundException, EventNotFoundException, ReactionAlreadyExistException, ReactionNotAvailableException;

    ReactionDto update(Long userId, Long eventId, TypeReaction reaction) throws ReactionNotFoundException, DuplicateReactionException;

    void delete(Long userId, Long eventId) throws ReactionNotFoundException;

    List<EventShortDto> getRatingEvents(TypeReaction typeReaction, Integer from, Integer size);

    List<UserRatingDto> getRatingUsers(TypeReaction typeReaction, Integer from, Integer size);
}
