package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.UserEmailNotUniqueException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto create(NewUserRequest userRequest) throws UserEmailNotUniqueException {
        try {
            UserDto userDto = userMapper.toUserDto(userRepository.save(userMapper.toUser(userRequest)));
            log.info("Create " + userDto);
            return userDto;
        } catch (DataIntegrityViolationException e) {
            throw new UserEmailNotUniqueException(e.getMessage());
        }
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<UserDto> list = userRepository.getAllByIds(ids, pageRequest)
                .stream().map(userMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Get users page= {}, size= {}", from, size);
        return list;
    }

    @Override
    public void delete(long userId) throws UserNotFoundException {
        checkExistsUser(userId);
        userRepository.deleteById(userId);
        log.info("Delete user id= {}", userId);
    }

    private void checkExistsUser(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException(UserNotFoundException.createMessage(id));
    }
}
