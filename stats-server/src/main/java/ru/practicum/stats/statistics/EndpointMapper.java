package ru.practicum.stats.statistics;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

@Mapper
public interface EndpointMapper {
    EndpointMapper INSTANCE = Mappers.getMapper(EndpointMapper.class);

    Endpoint toEndpoint(EndpointHit endpointHit);

    ViewStats toViewStats(View view);
}
