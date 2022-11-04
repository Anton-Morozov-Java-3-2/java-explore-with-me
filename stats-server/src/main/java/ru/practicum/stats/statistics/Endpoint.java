package ru.practicum.stats.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "endpoint_hits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "endpoint_timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "Endpoint{" +
                "id=" + id +
                ", app='" + app + '\'' +
                ", uri='" + uri + '\'' +
                ", ip='" + ip + '\'' +
                ", createDate=" + timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return id.equals(endpoint.id) && app.equals(endpoint.app) && uri.equals(endpoint.uri) && ip.equals(endpoint.ip)
                && timestamp.equals(endpoint.timestamp);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + app.hashCode() + uri.hashCode() + ip.hashCode() + timestamp.hashCode();
    }
}


