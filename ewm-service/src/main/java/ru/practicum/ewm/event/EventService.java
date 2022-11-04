package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    List<EventShortDto> getAll(String text,
                               List<Long> categories,
                               Boolean paid,
                               String rangeStart, String rangeEnd,
                               Boolean onlyAvailable,
                               SortType sort, Integer from, Integer size) throws DataTimeFormatException;

    EventFullDto getById(Long eventId) throws EventNotFoundException, EventStatusToViewException;

    void addViews(Long eventId, Long views) throws EventNotFoundException;

    List<EventShortDto> getAllByUserId(Long userId, Integer from, Integer size) throws UserNotFoundException;

    EventFullDto update(Long userId, UpdateEventRequest updateEventRequest) throws UserNotFoundException, CategoryNotFoundException, EventDateNotValidException, EventNotFoundException, EventStatusToEditException, UserAccessException;

    EventFullDto create(Long userId, NewEventDto newEventDto) throws UserNotFoundException, EventDateNotValidException, CategoryNotFoundException;

    EventFullDto getByEventId(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, UserAccessException;

    EventFullDto updateToCancel(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, UserAccessException, EventStatusToEditException;

    List<ParticipationRequestDto> getByUserIdEventId(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, UserAccessException;

    ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException, EventNotFoundException, UserAccessException, RequestNotFoundException, RequestConfirmationNotValidException;

    ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException, EventNotFoundException, UserAccessException, RequestNotFoundException, RequestConfirmationNotValidException;

    List<EventFullDto> getAll(List<Long> users, List<EventState> states, List<Long> categorise,
                              String rangeStart, String rangeEnd, Integer from, Integer size) throws DataTimeFormatException;

    EventFullDto put(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) throws EventNotFoundException, EventDataConstraintException;

    EventFullDto publish(Long eventId) throws EventNotFoundException, EventStatusToPublishException, EventPublishDateNotValidException;

    EventFullDto reject(Long eventId) throws EventNotFoundException, EventStatusToRejectException;
}
