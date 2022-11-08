package ru.practicum.stats.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Endpoint, Long> {
    @Query("select e.app as app, e.uri as uri, count(e) as hits from Endpoint e where (e.timestamp between :start and :end) " +
            "and (e.uri in (:uris)) group by e.app, e.uri")
    List<View> findByParameters(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);

    @Query("select e.app as app, e.uri as uri, count(e.uri) as hits from Endpoint e where (e.timestamp between :start and :end) " +
            "and (e.uri in (:uris)) group by e.app, e.uri, e.ip")
    List<View> findByParametersUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                @Param("uris") List<String> uris);
}
