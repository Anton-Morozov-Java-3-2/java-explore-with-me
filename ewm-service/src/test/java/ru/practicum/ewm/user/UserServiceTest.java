package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.exception.UserEmailNotUniqueException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    private MockitoSession session;

    private User user;

    private NewUserRequest userRequest;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(userRepository);
        user = new User(1L, "user@email.ru", "Виктор Комаров");
        userDto = UserMapper.INSTANCE.toUserDto(user);
        userRequest = new NewUserRequest("Виктор Комаров", "user@email.ru");
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    void create() throws UserEmailNotUniqueException {

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        Assertions.assertEquals(userDto,
                userService.create(userRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.getAllByIds(Mockito.any(List.class),Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl(List.of(user)));

        Assertions.assertArrayEquals(List.of(userDto).toArray(),
                userService.getAll(List.of(1L), 0, 10).toArray());

        Mockito.verify(userRepository, Mockito.times(1))
                .getAllByIds(Mockito.any(List.class),Mockito.any(PageRequest.class));
    }

    @Test
    void delete() {
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(true);

        Assertions.assertDoesNotThrow(() -> userService.delete(1L));


        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(Mockito.any(Long.class));
    }

    @Test
    void deleteUserNotFoundException() {
        Mockito.when(userRepository.existsById(Mockito.any(Long.class)))
                .thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,() -> userService.delete(1L));


        Mockito.verify(userRepository, Mockito.times(0))
                .deleteById(Mockito.any(Long.class));

    }

    @Test
    void createUserEmailNotUniqueException() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new DataIntegrityViolationException(""));

        Assertions.assertThrows(UserEmailNotUniqueException.class, () -> userService.create(userRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.any(User.class));
    }
}