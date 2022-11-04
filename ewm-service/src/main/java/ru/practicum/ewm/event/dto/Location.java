package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private double lat;
    private double lon;

    public Location(Integer lat, Integer lon) {
        this.lat = lat.doubleValue();
        this.lon = lon.doubleValue();
    }
}


