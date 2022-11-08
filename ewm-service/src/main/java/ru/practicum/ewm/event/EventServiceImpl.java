package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestState;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private static final  Long LIMIT_HOURS = 2L;

    private static final  Boolean DEFAULT_PAID = false;

    private static final  Boolean DEFAULT_REQUEST_MODERATION = true;

    private static final  Integer DEFAULT_PARTICIPANT_LIMIT = 0;

    private static final  LocalDateTime ZERO_DATE = LocalDateTime.of(0, 1, 1, 0, 0);

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    private final RequestMapper requestMapper;

    @Override
    public List<EventShortDto> getAll(String text,
                                      List<Long> categories,
                                      Boolean paid,
                                      String start,
                                      String end,
                                      Boolean onlyAvailable,
                                      SortType sort,
                                      Integer from, Integer size) throws DataTimeFormatException {

        PageRequest pageRequest = PageRequest.of(from, size);

        LocalDateTime now = LocalDateTime.now().withNano(0);

        try {
            LocalDateTime rangeStart = (start == null
                    ? now : LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            LocalDateTime rangeEnd = (end == null
                    ? (start == null ? now : ZERO_DATE)
                    : LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            log.info("Get all events by parameters: text= {} categories= {} paid={} start={} end={} " +
                            "onlyAvailable={} sort={} from={} size={}",
                    text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                    from, size);
            Page<Event> events = (sort == null ?
                    eventRepository.findAllByParameters(text, categories, paid, LocalDateTime.now(), rangeEnd,
                    onlyAvailable, pageRequest)
                    :
                    (sort == SortType.EVENT_DATE ?
                            eventRepository.findAllByParametersOrdersByEventDate(text, categories, paid, rangeStart, rangeEnd,
                            onlyAvailable, pageRequest)
                            :
                            eventRepository.findAllByParametersOrdersByViews(text, categories, paid, rangeStart, rangeEnd,
                            onlyAvailable, pageRequest)));

            log.info("Get events {}", events);
            return events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            throw new DataTimeFormatException("Format rangeStart: " + start + " and rangeEnd: " + end + " not validate");
        }
    }

    @Override
    public EventFullDto getById(Long eventId) throws EventNotFoundException, EventStatusToViewException {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStatusToViewException("Event must be published");
        }

        log.info("Get {}", event);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public void addViews(Long eventId, Long views) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        event.setViews(views);
        eventRepository.save(event);
        log.info("Add views={} eventId={}", views, eventId);
    }

    @Override
    public List<EventShortDto> getAllByUserId(Long userId, Integer from, Integer size) throws UserNotFoundException {
        checkExistsUser(userId);
        PageRequest pageRequest = PageRequest.of(from, size);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest).toList();
        log.info("Get all events by initiatorId={}", userId);
        return events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto update(Long userId, UpdateEventRequest updateEventRequest) throws UserNotFoundException,
            CategoryNotFoundException, EventDateNotValidException, EventNotFoundException, EventStatusToEditException,
            UserAccessException {

        checkExistsUser(userId);
        checkExistCategory(updateEventRequest.getCategory());
        checkEventDate(updateEventRequest.getEventDate());
        checkInitiator(userId, updateEventRequest.getEventId());

        Event event = eventRepository.findById(updateEventRequest.getEventId()).orElseThrow(
                () -> new EventNotFoundException(EventNotFoundException.createMessage(updateEventRequest.getEventId())));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStatusToEditException("Only pending or canceled events can be changed");
        }

        Event toUpdateEvent = eventMapper.toEvent(updateEventRequest);

        if (event.getState().equals(EventState.CANCELED)) {
            toUpdateEvent.setState(EventState.PENDING);
        }

        updateFields(toUpdateEvent, event);

        Event updateEvent = eventRepository.save(event);

        log.info("Update {}", updateEvent);
        return eventMapper.toEventFullDto(updateEvent);
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) throws UserNotFoundException,
            EventDateNotValidException, CategoryNotFoundException {

        User initiator = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(UserNotFoundException.createMessage(userId)));

        checkExistCategory(newEventDto.getCategory());
        checkEventDate(newEventDto.getEventDate());

        setDefaultParametersNewEventDto(newEventDto);

        Event event = eventMapper.toEvent(newEventDto);

        event.setInitiator(initiator);
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setViews(0L);

        defineStatusCreateEvent(event);
        definePublishedDate(event);

        Event newEvent = eventRepository.save(event);
        log.info("Create {}", newEvent);
        return eventMapper.toEventFullDto(newEvent);
    }

    @Override
    public EventFullDto getByEventId(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException,
            UserAccessException {
        checkExistsUser(userId);
        checkInitiator(userId, eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        log.info("Get {}", event);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateToCancel(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException,
            UserAccessException, EventStatusToEditException {
        checkExistsUser(userId);
        checkInitiator(userId, eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        if (event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
        } else {
            throw new EventStatusToEditException("Only pending events can be changed");
        }
        log.info("Event id={} canceled", event.getId());
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getByUserIdEventId(Long userId, Long eventId) throws UserNotFoundException,
            EventNotFoundException, UserAccessException {
        checkExistsUser(userId);
        checkInitiator(userId, eventId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        log.info("Get all requests on eventId={} ", eventId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException,
            EventNotFoundException, UserAccessException, RequestNotFoundException, RequestConfirmationNotValidException {

        checkExistsUser(userId);
        checkInitiator(userId, eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        Request request = requestRepository.findById(reqId).orElseThrow(
                () -> new RequestNotFoundException(RequestNotFoundException.createMessage(reqId)));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new RequestConfirmationNotValidException("Request not confirm. Moderation off or participant no limit");
        }

        if (event.getConfirmedRequests() == (long)event.getParticipantLimit()) {
            throw new RequestConfirmationNotValidException("Request not confirm. Participant limit exceeded");
        }

        request.setStatus(RequestState.CONFIRMED);
        Request confirmedRequest = requestRepository.save(request);
        log.info("Request id={} confirmed", reqId);

        Long confirmedRequests = event.getConfirmedRequests() + 1L;
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);

        if (event.getConfirmedRequests() == (long)event.getParticipantLimit()) {
            List<Request> requests = requestRepository.findAllByEventId(eventId);
            for (Request req : requests) {
                if (req.getStatus().equals(RequestState.PENDING)) {
                    req.setStatus(RequestState.REJECTED);
                    requestRepository.save(req);
                }
            }
        }
        return requestMapper.toParticipationRequestDto(confirmedRequest);
    }

    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException,
            EventNotFoundException, UserAccessException, RequestNotFoundException, RequestConfirmationNotValidException {
        checkExistsUser(userId);
        checkInitiator(userId, eventId);

        Request request = requestRepository.findById(reqId).orElseThrow(
                () -> new RequestNotFoundException(RequestNotFoundException.createMessage(reqId)));

        if (request.getStatus().equals(RequestState.CONFIRMED)) {
            Event event = eventRepository.findById(eventId).orElseThrow(() ->
                    new EventNotFoundException(EventNotFoundException.createMessage(eventId)));
            event.setConfirmedRequests(event.getConfirmedRequests() - 1L);
            eventRepository.save(event);
        }

        request.setStatus(RequestState.REJECTED);
        Request rejectRequest = requestRepository.save(request);
        log.info("Request with id={} rejected", request.getId());
        return requestMapper.toParticipationRequestDto(rejectRequest);
    }

    @Override
    public List<EventFullDto> getAll(List<Long> users,
                                     List<EventState> states,
                                     List<Long> categorise,
                                     String start,
                                     String end,
                                     Integer from, Integer size) throws DataTimeFormatException {

        try {
            LocalDateTime rangeStart = (start == null ? LocalDateTime.MIN : LocalDateTime.parse(URLDecoder.decode(start,
                    StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            LocalDateTime rangeEnd = (end == null ? LocalDateTime.MAX : LocalDateTime.parse(URLDecoder.decode(end,
                    StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            PageRequest pageRequest = PageRequest.of(from, size);
            log.info("Admin request all events by param users={} states={} categorise={} start={} end={} from={} size={}",
                    users, states, categorise, rangeStart, rangeEnd, from, size);

            List<Event> events = eventRepository.adminFindByParameters(users, states, categorise,
                    rangeStart, rangeEnd, pageRequest).toList();
            log.info("Get all events to admin {}", events);

            return events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            throw new DataTimeFormatException("Format rangeStart: " + start + " and rangeEnd: " + end + " not validate");
        }
    }

    @Override
    public EventFullDto put(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest)
            throws EventNotFoundException, EventDataConstraintException {
        Event toUpdateEvent = eventMapper.toEvent(adminUpdateEventRequest);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        updateFields(toUpdateEvent, event);
        if (toUpdateEvent.getLocationLat() != null) event.setLocationLat(toUpdateEvent.getLocationLat());
        if (toUpdateEvent.getLocationLon() != null) event.setLocationLon(toUpdateEvent.getLocationLon());

        try {
            Event adminUpdateEvent = eventRepository.save(event);
            log.info("Admin update {}", adminUpdateEvent);
            return eventMapper.toEventFullDto(adminUpdateEvent);
        } catch (DataIntegrityViolationException e) {
            throw new EventDataConstraintException(e.getMessage());
        }
    }

    private void updateFields(Event toUpdateEvent, Event event) {
        if (toUpdateEvent.getAnnotation() != null) event.setAnnotation(toUpdateEvent.getAnnotation());
        if (toUpdateEvent.getCategory() != null) event.setCategory(toUpdateEvent.getCategory());
        if (toUpdateEvent.getDescription() != null) event.setDescription(toUpdateEvent.getDescription());
        if (toUpdateEvent.getEventDate() != null) event.setEventDate(toUpdateEvent.getEventDate());
        if (toUpdateEvent.getPaid() != null) event.setPaid(toUpdateEvent.getPaid());
        if (toUpdateEvent.getParticipantLimit() != null) event.setParticipantLimit(toUpdateEvent.getParticipantLimit());
        if (toUpdateEvent.getTitle() != null) event.setTitle(toUpdateEvent.getTitle());
    }

    @Override
    public EventFullDto publish(Long eventId) throws EventNotFoundException, EventStatusToPublishException,
            EventPublishDateNotValidException {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new EventStatusToPublishException("Only pending events can be publishing");
        }

        LocalDateTime publishedDate = LocalDateTime.now();

        if (publishedDate.plusHours(2).isAfter(event.getEventDate())) {
            throw new EventPublishDateNotValidException(EventPublishDateNotValidException.createMessage(publishedDate));
        }

        event.setPublishedOn(publishedDate);
        event.setState(EventState.PUBLISHED);
        Event publishedEvent = eventRepository.save(event);
        log.info("Published {}", publishedEvent);
        return eventMapper.toEventFullDto(publishedEvent);
    }

    @Override
    public EventFullDto reject(Long eventId) throws EventNotFoundException, EventStatusToRejectException {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStatusToRejectException("Event with status published cannot be reject");
        }

        event.setState(EventState.CANCELED);
        Event canceledEvent = eventRepository.save(event);
        log.info("Canceled {}", canceledEvent);
        return eventMapper.toEventFullDto(canceledEvent);
    }


    private void checkExistsUser(Long userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(UserNotFoundException.createMessage(userId));
    }

    private void checkExistCategory(Long category_id) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(category_id)) {
            throw new CategoryNotFoundException(CategoryNotFoundException.createMessage(category_id));
        }
    }

    private void checkEventDate(LocalDateTime eventDate) throws EventDateNotValidException {
        if (LocalDateTime.now().plusHours(LIMIT_HOURS).isAfter(eventDate)) {
            throw new EventDateNotValidException(EventDateNotValidException
                    .createMessage(eventDate));
        }
    }

    private void checkInitiator(Long userId, Long eventId) throws EventNotFoundException, UserAccessException {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(EventNotFoundException.createMessage(eventId)));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new UserAccessException(UserAccessException.createMessage(userId));
        }
    }

    private void checkExistEvent(Long eventId) throws EventNotFoundException {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(EventNotFoundException.createMessage(eventId));
        }
    }

    private void setDefaultParametersNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(DEFAULT_PAID);
        }

        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(DEFAULT_PARTICIPANT_LIMIT);
        }

        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(DEFAULT_REQUEST_MODERATION);
        }
    }

    private void defineStatusCreateEvent(Event createEvent) {
        if (createEvent.getRequestModeration()) {
            createEvent.setState(EventState.PENDING);
        } else if (createEvent.getParticipantLimit() == 0) {
            createEvent.setState(EventState.PUBLISHED);
        } else {
            createEvent.setState(EventState.PENDING);
        }
    }

    private void definePublishedDate(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) event.setPublishedOn(LocalDateTime.now());
    }
}
