package ru.practicum.stats.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void create(EndpointHit endpointHint) {
        Endpoint endpoint = statsRepository.save(EndpointMapper.INSTANCE.toEndpoint(endpointHint));
        log.info("Crete endpointHit {}", endpoint);
    }

    @Override
    public List<ViewStats> get(String start, String end, List<String> uris, Boolean unique) {

        LocalDateTime rangeStart = LocalDateTime.parse(URLDecoder.decode(start,
                StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime rangeEnd = LocalDateTime.parse(URLDecoder.decode(end,
                StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<String> findUris = uris.stream().map(uri -> URLDecoder.decode(uri, StandardCharsets.UTF_8))
                .collect(Collectors.toList());

        log.info("Get hits by parameters: rangeStart= {} rangeEnd={} uris={} unique={}", rangeStart, rangeEnd, findUris,
                unique);
        List<View> views = (unique ? statsRepository.findByParametersUniqueIp(rangeStart, rangeEnd, findUris)
                : statsRepository.findByParameters(rangeStart, rangeEnd, findUris));

        List<ViewStats> viewStats = views.stream().map(EndpointMapper.INSTANCE::toViewStats).collect(Collectors.toList());

        log.info("Get hits: {}", viewStats);

        return viewStats;
    }
}
