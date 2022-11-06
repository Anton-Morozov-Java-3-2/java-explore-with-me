package ru.practicum.ewm.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.event.dto.*;

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
    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
