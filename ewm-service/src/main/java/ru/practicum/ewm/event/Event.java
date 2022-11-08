package ru.practicum.ewm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "confirmed_count")
    private Long confirmedRequests;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "location_lat", nullable = false)
    private Double locationLat;

    @Column(name = "location_lon", nullable = false)
    private Double locationLon;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "published_date")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "views", nullable = false)
    private long views;

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", annotation='" + annotation + '\'' +
                ", category=" + category +
                ", confirmedCount=" + confirmedRequests +
                ", createdOn=" + createdOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                ", description='" + description.substring(0, 52) + '\'' +
                ", eventData=" + eventDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                ", initiator=" + initiator +
                ", locationLat=" + locationLat +
                ", locationLon=" + locationLon +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", publishedOn=" + (publishedOn == null ? null : publishedOn
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) +
                ", requestModeration=" + requestModeration +
                ", title='" + title + '\'' +
                ", state='" + state + '\'' +
                ", views=" + views +
                '}';
    }
}
