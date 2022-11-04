package ru.practicum.stats.statistics;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

public interface StatsService {

    void create(EndpointHit endpointHint);

    List<ViewStats> get(String start, String end, List<String> uris, Boolean unique);
}
