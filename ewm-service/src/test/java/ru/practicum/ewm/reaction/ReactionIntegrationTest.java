package ru.practicum.ewm.reaction;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.reaction.dto.ReactionDto;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.RequestState;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.UserService;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.reaction.dto.UserRatingDto;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=ewm-db",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReactionIntegrationTest {
    private final EventService eventService;

    private final UserService userService;

    private final CategoryService categoryService;

    private final RequestService requestService;

    private final ReactionService reactionService;

    @Test
    void simpleTest() throws UserEmailNotUniqueException, UserNotFoundException, EventDateNotValidException,
            CategoryNotFoundException, CategoryNameNotUniqueException, EventNotFoundException, ParticipantLimitExceedException,
            UserAccessException, DuplicateRequestException, EventDataConstraintException, ReactionNotAvailableException,
            ReactionAlreadyExistException {

        UserDto user = userService.create(new NewUserRequest("user@email.ru", "user"));
        UserDto owner = userService.create(new NewUserRequest("owner@email.ru", "owner"));
        CategoryDto categoryDto = categoryService.create(new NewCategoryDto("test"));
        EventFullDto event1 = eventService.create(owner.getId(), new NewEventDto("test test test test test",
                                                                                 categoryDto.getId(),
                                                                                 "test test test test test",
                                                                                 LocalDateTime.now().plusDays(1),
                                                                                 new Location(1.1, 1.1),
                                                                                 true,
                                                                                 0,
                                                                                 false, "test"));

        ParticipationRequestDto requestDto = requestService.create(user.getId(), event1.getId());

        Assertions.assertEquals(requestDto.getStatus(), RequestState.CONFIRMED);

        AdminUpdateEventRequest updateEventRequest = new AdminUpdateEventRequest();
        updateEventRequest.setEventDate(LocalDateTime.now().minusDays(2));
        EventFullDto eventFullDto = eventService.put(event1.getId(), updateEventRequest);

        ReactionDto reactionDto = reactionService.create(user.getId(), eventFullDto.getId(), TypeReaction.LIKE);

        List<EventShortDto> eventShortDtoList = reactionService.getRatingEvents(TypeReaction.LIKE, 0, 10);

        Assertions.assertEquals(1, eventShortDtoList.size());
        Assertions.assertEquals(eventFullDto.getId(), eventShortDtoList.get(0).getId());
        Assertions.assertEquals(1L, eventShortDtoList.get(0).getLikes());


        List<UserRatingDto> userDtoList = reactionService.getRatingUsers(TypeReaction.LIKE, 0, 10);
        Assertions.assertEquals(1, userDtoList.size());
        Assertions.assertEquals(owner.getId(), userDtoList.get(0).getId());
        Assertions.assertEquals(1L, userDtoList.get(0).getRate());
    }
}
