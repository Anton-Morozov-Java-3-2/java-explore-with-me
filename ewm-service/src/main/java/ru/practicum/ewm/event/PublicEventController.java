package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.DataTimeFormatException;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.EventStatusToViewException;
import ru.practicum.ewm.stats.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    private final StatsClient statsClient;

    private static final String APP = "main";

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(name = "text", required = false) String text,
                                      @RequestParam(name = "categories", required = false) List<Long> categories,
                                      @RequestParam(name = "paid", required = false) Boolean paid,
                                      @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                      @RequestParam(name =  "rangeEnd", required = false) String rangeEnd,
                                      @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(name = "sort", required = false) SortType sort,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size,
                                      HttpServletRequest httpServletRequest)
            throws DataTimeFormatException {
            List<EventShortDto> eventShortDtoList = eventService.getAll(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, sort, from, size);
        statsClient.sendHit(APP, httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
        return eventShortDtoList;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable(name = "id") Long eventId,
                                HttpServletRequest httpServletRequest) throws EventStatusToViewException,
            EventNotFoundException {
        EventFullDto eventFullDto = eventService.getById(eventId);
        statsClient.sendHit(APP, httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
        eventService.addViews(eventId, statsClient.getStats(eventFullDto.getPublishedOn(), httpServletRequest.getRequestURI()));
        return eventFullDto;
    }
}
