package ru.practicum.ewm.reaction;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.reaction.dto.UserRatingDto;

import java.util.List;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
public class PublicReactionController {

    private final ReactionService reactionService;

    @GetMapping(path = "/events")
    List<EventShortDto> getRatingEvents(@RequestParam(value = "from",defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                           @RequestParam(value = "sort") TypeReaction typeReaction) {
        return reactionService.getRatingEvents(typeReaction, from, size);
    }

    @GetMapping(path = "/users")
    List<UserRatingDto> getRatingUsers(@RequestParam(value = "from",defaultValue = "0") Integer from,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                       @RequestParam(value = "sort") TypeReaction typeReaction) {
        return reactionService.getRatingUsers(typeReaction, from, size);
    }
}
