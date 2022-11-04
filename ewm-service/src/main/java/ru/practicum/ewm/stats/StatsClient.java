package ru.practicum.ewm.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public Long getStats(LocalDateTime published, String uri) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(rest.getUriTemplateHandler().expand("/") + "stats")
                .queryParam("start", published.format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", LocalDateTime.now().format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", List.of(uri))
                .queryParam("unique", false).toUriString();

        log.info("Get stats by uri: {}", urlTemplate);
        ViewStats[] viewStats = get(urlTemplate).getBody();
        if (viewStats == null) {
            return 0L;
        } else {
            return viewStats[0].getHits();
        }
    }

    public void sendHit(String app, String uri, String ip) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(rest.getUriTemplateHandler().expand("/") +
                "hit").toUriString();
        post(urlTemplate, new EndpointHit(app, uri, ip));
    }
}
