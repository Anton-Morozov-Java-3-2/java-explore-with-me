package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.exception.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(name = "users", required = false) List<Long> users,
                                      @RequestParam(name = "states", required = false) List<EventState> states,
                                      @RequestParam(name = "categories", required = false) List<Long> categorise,
                                      @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                      @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size)
            throws DataTimeFormatException {

        return eventService.getAll(users, states, categorise, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public EventFullDto put(@RequestBody AdminUpdateEventRequest adminUpdateEventRequest,
                             @PathVariable(name = "eventId") Long eventId) throws
            EventDataConstraintException, EventNotFoundException {
        return eventService.put(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto updateToPublish(@PathVariable(name = "eventId") Long eventId) throws
            EventPublishDateNotValidException, EventNotFoundException, EventStatusToPublishException {
        return eventService.publish(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto updateToReject(@PathVariable(name = "eventId") Long eventId) throws
            EventStatusToRejectException, EventNotFoundException {
        return eventService.reject(eventId);
    }
}
