package ru.practicum.ewm.reaction;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.reaction.dto.ReactionDto;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/reactions")
@RequiredArgsConstructor
public class PrivateReactionController {

    private final ReactionService reactionService;

    @PostMapping
    ReactionDto create(@PathVariable(name = "eventId") Long eventId,
                       @PathVariable(name = "userId") Long userId,
                       @RequestParam(name = "reaction") TypeReaction reaction) throws UserNotFoundException,
            EventNotFoundException, ReactionAlreadyExistException, ReactionNotAvailableException {
        return reactionService.create(userId, eventId, reaction);
    }

    @PatchMapping
    ReactionDto update(@PathVariable(name = "eventId") Long eventId,
                       @PathVariable(name = "userId") Long userId,
                       @RequestParam(name = "reaction") TypeReaction reaction) throws ReactionNotFoundException {
        return reactionService.update(userId, eventId, reaction);
    }

    @DeleteMapping
    void delete(@PathVariable(name = "eventId") Long eventId,
                @PathVariable(name = "userId") Long userId) throws ReactionNotFoundException {
        reactionService.delete(userId, eventId);
    }
}
