package ru.practicum.ewm.user;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(NewUserRequest userRequest);

    UserDto toUserDto(User user);
}
