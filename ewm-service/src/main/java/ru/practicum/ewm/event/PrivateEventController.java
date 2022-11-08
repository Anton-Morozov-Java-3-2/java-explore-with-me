package ru.practicum.ewm.event;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getAllByUserId(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size)
            throws UserNotFoundException {

        return eventService.getAllByUserId(userId, from, size);
    }

    @PatchMapping
    public EventFullDto update(@PathVariable(name = "userId") Long userId,
                               @Valid @RequestBody UpdateEventRequest updateEventRequest) throws UserNotFoundException,
            EventDateNotValidException, CategoryNotFoundException, EventNotFoundException, UserAccessException,
            EventStatusToEditException {

        return eventService.update(userId, updateEventRequest);
    }

    @PostMapping
    public EventFullDto create(@PathVariable(name = "userId") Long userId,
                               @Valid @RequestBody NewEventDto newEventDto) throws UserNotFoundException,
            EventDateNotValidException, CategoryNotFoundException {

        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable(name = "userId") Long userId,
                                @PathVariable(name = "eventId") Long eventId)
            throws UserNotFoundException, EventNotFoundException, UserAccessException {
        return eventService.getByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEvent(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "eventId") Long eventId) throws UserNotFoundException,
            EventNotFoundException, UserAccessException, EventStatusToEditException {
        return eventService.updateToCancel(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestByUserIdAndByEventId(@PathVariable(name = "userId") Long userId,
                                                        @PathVariable(name = "eventId") Long eventId) throws
            UserNotFoundException, EventNotFoundException, UserAccessException {
        return eventService.getByUserIdEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable(name = "userId") Long userId,
                                                  @PathVariable(name = "eventId") Long eventId,
                                                  @PathVariable(name = "reqId") Long reqId) throws UserNotFoundException,
            RequestConfirmationNotValidException, EventNotFoundException, RequestNotFoundException, UserAccessException {
        return eventService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable(name = "userId") Long userId,
                                                 @PathVariable(name = "eventId") Long eventId,
                                                 @PathVariable(name = "reqId") Long reqId) throws UserNotFoundException,
            RequestConfirmationNotValidException, EventNotFoundException, RequestNotFoundException, UserAccessException {
        return eventService.rejectRequest(userId, eventId, reqId);
    }
}
