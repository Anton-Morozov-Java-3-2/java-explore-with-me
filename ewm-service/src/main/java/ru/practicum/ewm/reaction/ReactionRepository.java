package ru.practicum.ewm.reaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.reaction.dto.EventRating;
import ru.practicum.ewm.reaction.dto.UserRating;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT e as event, COUNT(r) AS rate " +
            "FROM Event AS e LEFT JOIN Reaction AS r ON e.id = r.event.id WHERE r.reaction = ?1 GROUP BY e.id " +
            "ORDER BY rate")
    Page<EventRating> getRatingEvents(TypeReaction typeReaction, PageRequest pageRequest);

    @Query("SELECT e.initiator.id as id, e.initiator.email as email, e.initiator.name as name, COUNT(r) AS rate " +
            "FROM Event AS e LEFT JOIN Reaction AS r ON e.id = r.event.id WHERE r.reaction = ?1 GROUP BY e.initiator.id " +
            "ORDER BY rate")
    Page<UserRating> getRatingUsers(TypeReaction typeReaction, PageRequest pageRequest);
}
