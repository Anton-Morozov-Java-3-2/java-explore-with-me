package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getAllByUserId(Long userId) throws UserNotFoundException {
        checkExistsUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Get all request by user id={}", userId);
        return requests.stream().map(RequestMapper.INSTANCE::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) throws UserNotFoundException,
            EventNotFoundException, UserAccessException, ParticipantLimitExceedException, DuplicateRequestException {

        User requester = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(UserNotFoundException.createMessage(userId)));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new UserAccessException(
                    String.format("User with id=%d does not have access to create request for event with id=%d",
                            userId, eventId));
        }

        if (!requestRepository.findAllByEventIdAndRequesterId(eventId, userId).isEmpty()) {
            throw new DuplicateRequestException(
                    String.format("User with id=%d already created request for event with id=%d",
                            userId, eventId));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new UserAccessException(String.format("Event with id=%d not published", eventId));
        }

        if (event.getParticipantLimit() > 0L &&
                event.getParticipantLimit() == (long)event.getConfirmedRequests()) {
            throw new ParticipantLimitExceedException(ParticipantLimitExceedException.createMessage());
        }

        Request request = new Request();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        if (event.getRequestModeration()) {
            request.setStatus(RequestState.PENDING);
        } else if (event.getParticipantLimit() > 0 && event.getParticipantLimit() > event.getConfirmedRequests()) {
            request.setStatus(RequestState.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
            eventRepository.save(event);
        }

        Request createdRequest = requestRepository.save(request);
        log.info("Create " + request);
        return RequestMapper.INSTANCE.toParticipationRequestDto(createdRequest);
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long reqId) throws UserNotFoundException,
            RequestNotFoundException, UserAccessException, EventNotFoundException {
        checkExistsUser(userId);

        Request request = requestRepository.findById(reqId).orElseThrow(
                () -> new RequestNotFoundException(RequestNotFoundException.createMessage(reqId)));

        if (!request.getRequester().getId().equals(userId)) {
            throw new UserAccessException(
                    String.format("User with id=%d does not have access to reject request with id=%d", userId, reqId));
        }
        if (request.getStatus().equals(RequestState.CONFIRMED)) {
            Long eventId = request.getEvent().getId();
            Event event = eventRepository.findById(eventId).orElseThrow(() ->
                    new EventNotFoundException(EventNotFoundException.createMessage(eventId)));
            event.setConfirmedRequests(event.getConfirmedRequests() - 1L);
            eventRepository.save(event);
        }

        request.setStatus(RequestState.CANCELED);
        Request cancelRequest = requestRepository.save(request);
        log.info("Cancel {}", request);
        return RequestMapper.INSTANCE.toParticipationRequestDto(cancelRequest);
    }

    private void checkExistsUser(Long userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(UserNotFoundException.createMessage(userId));
    }
}
