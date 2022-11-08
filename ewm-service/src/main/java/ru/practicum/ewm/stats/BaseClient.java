package ru.practicum.ewm.stats;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<ViewStats[]> get(String url) {
        return rest.getForEntity(url, ViewStats[].class);
    }

    protected void post(String url, EndpointHit endpointHit) {
        HttpEntity<EndpointHit> request = new HttpEntity<>(endpointHit);
        rest.postForObject(url, request, EndpointHit.class);
    }
}
