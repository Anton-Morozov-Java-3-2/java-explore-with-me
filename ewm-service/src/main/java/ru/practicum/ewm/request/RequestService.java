package ru.practicum.ewm.request;

import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getAllByUserId(Long userId) throws UserNotFoundException;

    ParticipationRequestDto create(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException,
            UserAccessException, ParticipantLimitExceedException, DuplicateRequestException;

    ParticipationRequestDto cancel(Long userId, Long reqId) throws UserNotFoundException, RequestNotFoundException,
            UserAccessException, EventNotFoundException;
}
