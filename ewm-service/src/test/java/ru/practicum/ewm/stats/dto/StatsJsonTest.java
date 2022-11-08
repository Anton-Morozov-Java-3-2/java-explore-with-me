package ru.practicum.ewm.stats.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatsJsonTest {

    @Autowired
    private JacksonTester<EndpointHit> endpointHitJson;

    @Autowired
    private JacksonTester<ViewStats> viewsStatsJson;


    @Test
    void testSerialize() throws Exception {
        var endpointHit = new EndpointHit("main", "event/1", "178.158.43.000");
        endpointHit.setId(1L);
        var viewStats = new ViewStats("main", "event/1", 4L);

        var resultEndPoint = endpointHitJson.write(endpointHit);
        var resultViews = viewsStatsJson.write(viewStats);

        assertThat(resultEndPoint).hasJsonPath("$.id");
        assertThat(resultEndPoint).hasJsonPath("$.app");
        assertThat(resultEndPoint).hasJsonPath("$.uri");
        assertThat(resultEndPoint).hasJsonPath("$.ip");

        assertThat(resultViews).hasJsonPath("$.app");
        assertThat(resultViews).hasJsonPath("$.uri");
        assertThat(resultViews).hasJsonPath("$.hits");

        assertThat(resultEndPoint).extractingJsonPathStringValue("$.app").isEqualTo(endpointHit.getApp());
        assertThat(resultEndPoint).extractingJsonPathStringValue("$.uri").isEqualTo(endpointHit.getUri());
        assertThat(resultEndPoint).extractingJsonPathStringValue("$.ip").isEqualTo(endpointHit.getIp());
        assertThat(resultEndPoint).extractingJsonPathNumberValue("$.id").isEqualTo(endpointHit.getId()
                .intValue());

        assertThat(resultViews).extractingJsonPathStringValue("$.app").isEqualTo(viewStats.getApp());
        assertThat(resultViews).extractingJsonPathStringValue("$.uri").isEqualTo(viewStats.getUri());
        assertThat(resultViews).extractingJsonPathNumberValue("$.hits").isEqualTo(viewStats.getHits()
                .intValue());
    }

}