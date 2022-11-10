package ru.practicum.ewm.reaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.reaction.dto.ReactionDto;
import ru.practicum.ewm.reaction.dto.UserRatingDto;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestState;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final ReactionMapper reactionMapper;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    @Override
    public ReactionDto create(Long userId, Long eventId, TypeReaction newReaction) throws UserNotFoundException,
            EventNotFoundException, ReactionAlreadyExistException, ReactionNotAvailableException {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                UserNotFoundException.createMessage(userId)));

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                EventNotFoundException.createMessage(eventId)));

        if (reactionRepository.findByEventIdAndUserId(eventId, userId).isPresent()) {
            throw new ReactionAlreadyExistException(ReactionAlreadyExistException.createMessage(userId, eventId));
        }

        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ReactionNotAvailableException("Reactions are not available before the event");
        }

        Optional<Request> optionalRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);

        if (optionalRequest.isEmpty()) {
            throw new ReactionNotAvailableException("Reactions are available only to users who attended the event");
        }

        Request request = optionalRequest.get();

        if (!request.getStatus().equals(RequestState.CONFIRMED)) {
            throw new ReactionNotAvailableException("Reactions are available only to users who attended the event");
        }

        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setEvent(event);
        reaction.setReaction(newReaction);

        ReactionDto reactionDto = reactionMapper.toDto(reactionRepository.save(reaction));
        log.info("Create {}", reactionDto);
        return reactionDto;
    }

    @Override
    public ReactionDto update(Long userId, Long eventId, TypeReaction newReaction) throws ReactionNotFoundException {
        Optional<Reaction> optionalReaction = reactionRepository.findByEventIdAndUserId(eventId, userId);
        if (optionalReaction.isEmpty()) {
            throw new ReactionNotFoundException(ReactionNotFoundException.createMessage(userId, eventId));
        }
        Reaction reaction = optionalReaction.get();
        reaction.setReaction(newReaction);
        ReactionDto reactionDto = reactionMapper.toDto(reactionRepository.save(reaction));
        log.info("Update {}", reactionDto);
        return reactionDto;
    }

    @Override
    public void delete(Long userId, Long eventId) throws ReactionNotFoundException {
        Optional<Reaction> reaction = reactionRepository.findByEventIdAndUserId(eventId, userId);
        if (reaction.isEmpty()) {
            throw new ReactionNotFoundException(ReactionNotFoundException.createMessage(userId, eventId));
        }
        reactionRepository.deleteById(reaction.get().getId());
        log.info("Delete {}", reaction.get());
    }

    @Override
    public List<EventShortDto> getRatingEvents(TypeReaction typeReaction, Integer from, Integer size) {
        int page = from / size;
        List<EventShortDto> events = reactionRepository.getRatingEvents(typeReaction, PageRequest.of(page, size))
                .stream()
                .map(eventRating -> {
                    EventShortDto eventShortDto = eventMapper.toEventShortDto(eventRating.getEvent());
                    if (typeReaction.equals(TypeReaction.LIKE))
                        eventShortDto.setLikes(eventRating.getRate());
                    else {
                        eventShortDto.setDislikes(eventRating.getRate());
                    }
                    return eventShortDto;
                })
                .collect(Collectors.toList());
        log.info("Get rating events by {} {}", typeReaction, events);
        return events;
    }

    @Override
    public List<UserRatingDto> getRatingUsers(TypeReaction typeReaction, Integer from, Integer size) {
        Integer page = from / size;
        List<UserRatingDto> users = reactionRepository.getRatingUsers(typeReaction, PageRequest.of(page, size))
                .stream().map(userRating -> new UserRatingDto(userRating.getId(),
                        userRating.getEmail(), userRating.getName(), userRating.getRate()))
                .collect(Collectors.toList());
        log.info("Get rating users by {} {}", typeReaction, users);
        return users;
    }
}
