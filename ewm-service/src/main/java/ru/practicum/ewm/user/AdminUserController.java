package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.UserEmailNotUniqueException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    List<UserDto> getAll(@RequestParam(value = "ids", required = false) List<Long> ids,
                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                         @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return userService.getAll(ids, from, size);
    }

    @PostMapping
    UserDto create(@Valid @RequestBody NewUserRequest userRequest) throws UserEmailNotUniqueException {
        return userService.create(userRequest);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) throws UserNotFoundException {
        userService.delete(userId);
    }
}
