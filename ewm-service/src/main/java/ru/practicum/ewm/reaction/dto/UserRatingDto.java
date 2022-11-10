package ru.practicum.ewm.reaction.dto;

import lombok.Value;

@Value
public class UserRatingDto {
    Long id;
    String email;
    String name;
    Long rate;
}
