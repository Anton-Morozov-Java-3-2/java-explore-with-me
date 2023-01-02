package ru.practicum.ewm.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.reaction.Reaction;
import ru.practicum.ewm.reaction.TypeReaction;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "locationLon", source = "location.lon")
    @Mapping(target = "locationLat", source = "location.lat")
    Event toEvent(NewEventDto newEventDto);

    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "category.id", source = "category")
    Event toEvent(UpdateEventRequest updateEventRequest);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "locationLon", source = "location.lon")
    @Mapping(target = "locationLat", source = "location.lat")
    Event toEvent(AdminUpdateEventRequest adminUpdateEventRequest);

    @Mapping(target = "location.lon", source = "locationLon")
    @Mapping(target = "location.lat", source = "locationLat")
    @Mapping(target = "likes", source = "reactions",  qualifiedByName = "reactionsToLikes")
    @Mapping(target = "dislikes", source = "reactions", qualifiedByName = "reactionsToDislikes")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "likes", source = "reactions",  qualifiedByName = "reactionsToLikes")
    @Mapping(target = "dislikes", source = "reactions", qualifiedByName = "reactionsToDislikes")
    EventShortDto toEventShortDto(Event event);

    @Named("reactionsToLikes")
    default Long reactionsToLike(Set<Reaction> reactions) {
        return reactions.stream().filter(reaction -> reaction.getReaction().equals(TypeReaction.LIKE)).count();
    }

    @Named("reactionsToDislikes")
    default Long reactionsToDislike(Set<Reaction> reactions) {
        return reactions.stream().filter(reaction -> reaction.getReaction().equals(TypeReaction.DISLIKE)).count();
    }
}
