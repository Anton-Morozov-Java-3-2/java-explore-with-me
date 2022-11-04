package ru.practicum.ewm.user;
import ru.practicum.ewm.exception.UserEmailNotUniqueException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest userRequest) throws UserEmailNotUniqueException;

    List<UserDto> getAll(List<Long> ids, int from, int size);

    void delete(long userId) throws UserNotFoundException;
}
