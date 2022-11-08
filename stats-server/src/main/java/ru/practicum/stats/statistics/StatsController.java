package ru.practicum.stats.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;

    @GetMapping(path = "/stats")
    public List<ViewStats> get(@RequestParam(name = "start", required = false) String start,
                               @RequestParam(name = "end", required = false) String end,
                               @RequestParam(name = "uris", required = false) List<String> uris,
                               @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        return statService.get(start, end, uris, unique);
    }

    @PostMapping(path = "/hit")
    public void create(@Valid @RequestBody EndpointHit endpointHint) {
        statService.create(endpointHint);
    }
}
