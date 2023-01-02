package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("select e from Event e where " +
            "(" +
            "(:text is null or lower(e.annotation) like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%')))" +
            "and (:cat is null or e.category.id in (:cat))" +
            "and (:paid is null or e.paid = :paid ) " +
            "and (e.eventDate >= :start) " +
            "and (e.eventDate <= :end) " +
            "and (:avl = false or e.confirmedRequests < e.participantLimit) and (e.state = 'PUBLISHED')" +
            ") order by e.views desc")
    Page<Event> findAllByParametersOrdersByViews(@Param("text")String text,
                                                @Param("cat") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("start") LocalDateTime rangeStart,
                                                @Param("end") LocalDateTime rangeEnd,
                                                @Param("avl") Boolean onlyAvailable,
                                                Pageable pageable);

    @Query("select e from Event e where " +
            "(" +
            "(:text is null or lower(e.annotation) like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%')))" +
            "and (:cat is null or e.category.id in (:cat))" +
            "and (:paid is null or e.paid = :paid ) " +
            "and (e.eventDate >= :start) " +
            "and (e.eventDate <= :end) " +
            "and (:avl = false or e.confirmedRequests < e.participantLimit) and (e.state = 'PUBLISHED')" +
            ") order by e.eventDate desc")
    Page<Event> findAllByParametersOrdersByEventDate(@Param("text")String text,
                                                     @Param("cat") List<Long> categories,
                                                     @Param("paid") Boolean paid,
                                                     @Param("start") LocalDateTime rangeStart,
                                                     @Param("end") LocalDateTime rangeEnd,
                                                     @Param("avl") Boolean onlyAvailable,
                                                     Pageable pageable);

    @Query("select e from Event e where " +
            "(" +
            "(:text is null or lower(e.annotation) like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%')))" +
            "and (:cat is null or e.category.id in (:cat))" +
            "and (:paid is null or e.paid = :paid ) " +
            "and (e.eventDate >= :start) " +
            "and (e.eventDate <= :end) " +
            "and (:avl = false or e.confirmedRequests < e.participantLimit) and (e.state = 'PUBLISHED'))")
    Page<Event> findAllByParameters(@Param("text")String text,
                                    @Param("cat") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("start") LocalDateTime rangeStart,
                                    @Param("end") LocalDateTime rangeEnd,
                                    @Param("avl") Boolean onlyAvailable,
                                    Pageable pageable);

    @Query("select e from Event e where " +
            "((:userIds is null or e.initiator.id in (:userIds)) " +
            "and (:state is null or e.state in (:state)) " +
            "and (:categoryIds is null or e.category.id in (:categoryIds)) " +
            "and e.eventDate >= :startRange and e.eventDate <= :endRange)" +
            "")
    Page<Event> adminFindByParameters(@Param("userIds") List<Long> users,
                                      @Param("state") List<EventState> states,
                                      @Param("categoryIds") List<Long> categories,
                                      @Param("startRange") LocalDateTime start,
                                      @Param("endRange") LocalDateTime end,
                                      Pageable pageable);

    @Query("select e from Event e where :userIds is null or e.initiator.id in (:userIds)")
    Set<Event> adminFindByIds(@Param("userIds") Set<Long> users);
}
