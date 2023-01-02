package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);
}
