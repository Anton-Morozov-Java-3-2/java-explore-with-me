package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getAllByUserId(@PathVariable(name = "userId") Long userId)
            throws UserNotFoundException {
        return requestService.getAllByUserId(userId);
    }

    @PostMapping
    public ParticipationRequestDto create(@PathVariable(name = "userId") Long userId,
                                          @RequestParam(name = "eventId") Long eventId)
            throws UserNotFoundException, EventNotFoundException, ParticipantLimitExceedException,
            UserAccessException, DuplicateRequestException {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable(name = "userId") Long userId,
                                          @PathVariable(name = "requestId") Long reqId)
            throws UserNotFoundException, RequestNotFoundException, EventNotFoundException, UserAccessException {
        return requestService.cancel(userId, reqId);
    }
}
